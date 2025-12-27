package com.capstone.ecommerce.product.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;

@Profile("bonsai")
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    String elasticsearchUrl;

    @Value("${spring.elasticsearch.username}")
    String username;

    @Value("${spring.elasticsearch.password}")
    String password;


    @Bean
    public ElasticsearchClient elasticsearchClient() {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        Header[] headers = new Header[]{
                new BasicHeader(
                        "Content-Type",
                        "application/json"
                )
        };

        var uri = URI.create(elasticsearchUrl);

        RestClient restClient = RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                .setDefaultHeaders(headers)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        ElasticsearchTransport transport =
                new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
