package io.jgitkins.web.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

	@Bean
	public RestClient jgitkinsRestClient(JgitkinsServerProperties properties, RestClient.Builder builder) {
		return builder.baseUrl(properties.baseUrl().toString()).build();
	}
}
