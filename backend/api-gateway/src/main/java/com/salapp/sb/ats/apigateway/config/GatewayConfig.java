package com.salapp.sb.ats.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class GatewayConfig {

    /*@Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, RestTemplate restTemplate) {
        return builder.routes()
                .route("user_service", r -> r.path("/users/**")
                        .filters( f-> f.filter((exchange, chain) -> {
                            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.PROXY_AUTHORIZATION);
                            if(token != null && token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                restTemplate.postForObject("http://localhost:8080/auth/validate", new AuthRequest(token), AuthResponse.class
                                );


                            }
                        })))
                .build();
    }*/

}
