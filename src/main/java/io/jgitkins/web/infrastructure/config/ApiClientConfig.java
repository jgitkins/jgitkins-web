package io.jgitkins.web.infrastructure.config;

import io.jgitkins.web.infrastructure.security.SessionTokenProvider;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
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
				.requestInterceptor((request, body, execution) -> {
					long startTime = System.currentTimeMillis();
					log.debug("HTTP Client -> Method: [{}], URI: [{}]", request.getMethod(), request.getURI());
					ClientHttpResponse response = null;
					try {
						response = execution.execute(request, body);
						return response;
					} catch (IOException ex) {
						log.debug("HTTP Client <- Error: [{}], URI: [{}]", ex.getClass().getSimpleName(), request.getURI());
						throw ex;
					} finally {
						long durationMs = System.currentTimeMillis() - startTime;
						if (response != null) {
							try {
								log.debug("HTTP Client <- Status: [{}], URI: [{}], DurationMs: [{}]",
										response.getStatusCode(), request.getURI(), durationMs);
							} catch (IOException ex) {
								log.debug("HTTP Client <- Status: [unknown], URI: [{}], DurationMs: [{}]",
										request.getURI(), durationMs);
							}
						}
					}
				})
				.build();
	}
}
