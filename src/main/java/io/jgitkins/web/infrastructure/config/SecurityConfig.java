package io.jgitkins.web.infrastructure.config;

import io.jgitkins.web.infrastructure.config.security.handler.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
												   OAuth2LoginSuccessHandler successHandler) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/", "/explore", "/explore/**", "/assets/**", "/login", "/error", "/error/**", "/actuator/prometheus").permitAll()
						.requestMatchers(new RegexRequestMatcher("^/(?!assets|css|js|img|svg|favicon\\.ico|webjars|settings|notifications|explore|fragments|repositories|oauth2|login|error|actuator)([^/]+)$", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher("^/(?!assets|css|js|img|svg|favicon\\.ico|webjars|settings|notifications|explore|fragments|repositories|oauth2|login|error|actuator)([^/]+)/-/.*$", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher("^/(?!assets|css|js|img|svg|favicon\\.ico|webjars|settings|notifications|explore|fragments|repositories|oauth2|login|error|actuator)([^/]+)/([^/]+)$", null)).permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(login -> login
						.loginPage("/oauth2/authorization/google")
						.successHandler(successHandler)
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/")
				)
				.csrf(Customizer.withDefaults());
		return http.build();
	}
}
