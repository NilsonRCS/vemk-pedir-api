package com.vemk_pedir.api;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toH2Console()).permitAll()
				.anyRequest().authenticated())
			.csrf(csrf -> csrf.disable())
			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
			.httpBasic(Customizer.withDefaults())
			.formLogin(form -> form.disable());

		return http.build();
	}
}