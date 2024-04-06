package org.booking.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.booking.core.response.LoggedInResponse;
import org.booking.core.service.AuthenticationService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Log
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthConfig> {

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER_ = "Bearer ";

	private final AuthenticationService authenticationService;

	@Override
	public GatewayFilter apply(AuthConfig config) {
		return (exchange, chain) -> {
			final ServerHttpRequest request = exchange.getRequest();

			final boolean authorization = request.getHeaders().containsKey("Authorization");
			log.info(AUTHORIZATION + ": " + authorization);
			List<String> strings = request.getHeaders().get(AUTHORIZATION);
			String authHeader = strings.get(0);
			if (!authHeader.contains(BEARER_)) {
				return this.onError(exchange, "No token header", HttpStatus.UNAUTHORIZED);
			}
			var jwtToken = authHeader.substring(7);
			LoggedInResponse loggedInResponse;
			try {
				 loggedInResponse = authenticationService.authenticate(jwtToken);
				 log.info("Authenticated request");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			String userEmail = loggedInResponse.getUserEmail();

			List<String> role = loggedInResponse.getRole();
			// todo check access to url by roles
			try {
				final ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
						.header(AUTHORIZATION, userEmail).build();
				return chain.filter(exchange.mutate().request(modifiedRequest).build());
			} catch (Exception e) {
				log.warning(e.getMessage());
				return this.onError(exchange, "Modified Request " + e.getMessage(), HttpStatus.UNAUTHORIZED);
			}
		};
	}

	private Mono<Void> onError(final ServerWebExchange exchange, final String err, final HttpStatus httpStatus) {
		log.warning("Gateway Auth Error {}" + err);
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();
	}

}
