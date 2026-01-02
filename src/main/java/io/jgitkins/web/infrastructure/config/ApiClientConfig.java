package io.jgitkins.web.infrastructure.config;

import io.jgitkins.web.infrastructure.security.SessionTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

	@Bean
	public RestClient jgitkinsRestClient(JgitkinsServerProperties properties,
										 RestClient.Builder builder,
										 SessionTokenProvider tokenProvider) {
		return builder
				.baseUrl(properties.baseUrl().toString())
				.requestInterceptor((request, body, execution) -> {
					String token = tokenProvider.getToken();
					if (token != null && !token.isBlank()) {
						request.getHeaders().setBearerAuth(token);
					}
					return execution.execute(request, body);
				})
				.build();
	}
}
