package org.booking.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Log
@RequiredArgsConstructor
@Configuration
public class RouteConfig {

	private final AuthenticationFilter authenticationFilter;
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(authRoute())
				.route(managementsRoute())
				.route(customersRoute())
				.build();
	}

	private Function<PredicateSpec, Buildable<Route>> customersRoute() {
		log.info("Invoke managementsRoute");
		return p -> p
				.path("/api/v1/customers/**")
				.filters(f -> f.filter(
						authenticationFilter.apply(new AuthConfig())
				))
				.uri("lb://BOOKING-CORE-V2");
	}

	private Function<PredicateSpec, Buildable<Route>> managementsRoute() {
		log.info("Invoke managementsRoute");
		return p -> p
				.path("/api/v1/managements/**")
				.filters(f -> f.filter(
						authenticationFilter.apply(new AuthConfig())
				))
				.uri("lb://BOOKING-CORE-V2");
	}

	private Function<PredicateSpec, Buildable<Route>> authRoute() {
		log.info("Invoke authRoute");
		return p -> p
				.path("/api/v1/auth/**")
				.uri("lb://BOOKING-CORE-AUTHENTICATION");
	}
}
