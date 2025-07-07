package com.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class securityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (REST API용)

                // .authorizeHttpRequests(authorize -> authorize
                // .requestMatchers("/", "/sample/guest").permitAll()
                // .requestMatchers("/sample/member").hasRole("USER")
                // .requestMatchers("/sample/admin").hasRole("ADMIN")
                // .anyRequest().authenticated())
                // .httpBasic(Customizer.withDefaults());
                // .formLogin(Customizer.withDefaults()); // 시큐리티가 제공하는 기본 로그인 폼 페이지
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/image/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board", "/board/**", "/comment/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/board/**", "/comment/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/board/**", "/comment/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/board/**", "/comment/**").permitAll()
                        .anyRequest().permitAll());
        // .authenticated()

        // http.logout(logout -> logout
        // .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
        // .logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean // = new 한 뒤 스프링 컨데이너가 관리
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        RememberMeTokenAlgorithm encodingAlgorithm = RememberMeTokenAlgorithm.SHA256;
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("myKey",
                userDetailsService, encodingAlgorithm);
        rememberMeServices.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5);
        rememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 7);// 일주일 기억
        return rememberMeServices;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // React 개발 서버 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}