package com.example.server.config;

import com.example.server.security.JwtAuthenticationFilter;
import com.example.server.security.JwtTokenProvider;
import com.example.server.security.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;

@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

        private final MemberDetailsService memberDetailsService;
        private final JwtTokenProvider jwtTokenProvider;
        private final BeanConfig beanConfig;

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
                return new JwtAuthenticationFilter(jwtTokenProvider);
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
                auth.userDetailsService(memberDetailsService)
                                .passwordEncoder(beanConfig.passwordEncoder());
                return auth.build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (REST API용)
                                .cors(Customizer.withDefaults())
                                .sessionManagement(sm -> sm
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/css/**", "/js/**", "/image/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/board", "/board/**")
                                                .permitAll()
                                                .requestMatchers("/auth/register", "/auth/login", "/auth/verify/**")
                                                .permitAll()
                                                .requestMatchers("/error").permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(
                                                new JwtAuthenticationFilter(jwtTokenProvider),
                                                UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

                return http.build();
        }

        // @Bean
        // public RememberMeServices rememberMeServices(UserDetailsService
        // userDetailsService) {
        // RememberMeTokenAlgorithm encoding = RememberMeTokenAlgorithm.SHA256;
        // TokenBasedRememberMeServices rm = new TokenBasedRememberMeServices("myKey",
        // userDetailsService, encoding);
        // rm.setMatchingAlgorithm(RememberMeTokenAlgorithm.SHA256);
        // rm.setTokenValiditySeconds(7 * 24 * 60 * 60); // 일주일
        // return rm;
        // }
}
