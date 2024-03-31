package org.booking.core.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/api/v1/auth/**")
						.uri("lb://BOOKING-CORE-V2"))
				.route(p -> p
						.path("/api/v1/managements/**")
						.uri("lb://BOOKING-CORE-V2"))
				.route(p -> p
						.path("/api/v1/customers/**")
						.uri("lb://BOOKING-CORE-V2"))
				.build();
	}
}
