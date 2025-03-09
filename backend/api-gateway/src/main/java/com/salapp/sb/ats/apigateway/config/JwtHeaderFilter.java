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
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-Roles";
    private static final String HEADER_PERMISSIONS = "X-Permissions";

    public JwtHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("JwtHeaderFilter config: {}", config);
        return ((exchange, chain) -> {
            log.debug("Principal: {}", exchange.getPrincipal());

            return exchange.getPrincipal()
                    .cast(JwtAuthenticationToken.class)
                    .map(jwtAuthToken -> {
                        String userId = jwtAuthToken.getToken().getSubject();
                        List<String> roles = jwtAuthToken.getAuthorities().stream()
                                .filter(auth -> auth.getAuthority().startsWith("ROLE_")) // Filter roles
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                        List<String> authorities = jwtAuthToken.getAuthorities().stream()
                                .filter(auth -> !auth.getAuthority().startsWith("ROLE_")) // Filter permissions
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                        // Add headers to the request
                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HEADER_USER_ID, userId);
                        headers.add(HEADER_ROLES, String.join(",", roles));
                        headers.add(HEADER_PERMISSIONS, String.join(",", authorities));

                        log.info("Add headers to request: {}", headers);

                        return exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                                        .build())
                                .build();
                    }).defaultIfEmpty(exchange)
                    .flatMap(chain::filter);
        });
    }

    public static class Config {

    }
}
