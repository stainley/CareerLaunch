package com.salapp.sb.ats.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtHeaderFilter extends AbstractGatewayFilterFactory<JwtHeaderFilter.Config> {

    public JwtHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(jwtAuthToken -> {
                    String userId = jwtAuthToken.getToken().getSubject();
                    List<String> roles = jwtAuthToken.getAuthorities().stream()
                            .filter(auth -> auth.getAuthority().startsWith("ROLE_")) // Filter roles
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    List<String> permissions = jwtAuthToken.getAuthorities().stream()
                            .filter(auth -> !auth.getAuthority().startsWith("ROLE_")) // Filter permissions
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());

                    // Add headers to the request
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-User-Id", userId);
                    headers.add("X-Roles", String.join(",", roles));
                    headers.add("X-Permissions", String.join(",", permissions));

                    log.info("headers: {}", headers);

                    return exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                                    .build())
                            .build();
                }).defaultIfEmpty(exchange)
                .flatMap(chain::filter));
    }

    public static class Config {

    }
}
