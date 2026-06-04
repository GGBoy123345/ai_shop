package com.sxpi.pan.aimallsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Value("${spring.elasticsearch.uris}")
    private String esUri;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        String uri = esUri.replace("http://", "").replace("https://", "");
        String[] parts = uri.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        String scheme = esUri.startsWith("https") ? "https" : "http";

        return RestClient.builder(new HttpHost(host, port, scheme)).build();
    }
}
