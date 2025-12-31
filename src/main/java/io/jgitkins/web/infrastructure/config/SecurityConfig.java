package io.jgitkins.web.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/", "/explore", "/css/**", "/img/**", "/svg/**","/login", "/error").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(login -> login
						.loginPage("/oauth2/authorization/google")
						.defaultSuccessUrl("/dashboard", true)
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/")
				)
				.csrf(Customizer.withDefaults());
		return http.build();
	}
}
