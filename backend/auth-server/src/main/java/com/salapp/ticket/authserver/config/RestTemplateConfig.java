package com.salapp.ticket.authserver.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream trustStoreStream = getClass().getClassLoader().getResourceAsStream("auth-truststore.jks")) {
            trustStore.load(trustStoreStream, "authpass".toCharArray());
        }

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(trustStore, null)
                .build();


        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();

        log.info("SSL Context: {}", sslContext.getProvider());

        ClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        return new RestTemplate();
    }
}