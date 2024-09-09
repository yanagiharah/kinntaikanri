package com.example.demo.security;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	/** パスワードエンコーダー */
	private final PasswordEncoder passwordEncoder;

	/** ユーザー情報取得Service */
	private final UserDetailsService userDetailsService;

	/** メッセージ取得 */
	private final MessageSource messageSource;
	

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(login -> login
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccess())  
                .usernameParameter("userId")
                .passwordParameter("password")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout").permitAll()
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**","/dateOut").permitAll()
               // .requestMatchers("/menu/**").hasRole("ADMIN")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccess() {
        return new CustomAuthentication();  // 成功時メソッドを使用
    }

    @Bean
	AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setMessageSource(messageSource);
		return provider;
	}

 
}
