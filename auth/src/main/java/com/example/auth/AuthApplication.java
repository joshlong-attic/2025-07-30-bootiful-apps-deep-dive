package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    SecurityFilterChain http(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(ae -> ae.anyRequest().authenticated())
                .with(authorizationServer(), as -> as.oidc(Customizer.withDefaults()))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .webAuthn(a -> a
                        .allowedOrigins("http://localhost:9090")
                        .rpId("localhost")
                        .rpName("Bootiful")
                )
                .oneTimeTokenLogin(ott -> ott.tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
                    response.getWriter().write("you've got console mail!");
                    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                    System.out.println("please go to http://localhost:9090/login/ott?token=" + oneTimeToken.getTokenValue());
                }))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder pw) {
        var users = Set.of(
                User
                        .withUsername("jlong")
                        .password(pw.encode("pw"))
                        .roles("USER")
                        .build()
        );
        return new InMemoryUserDetailsManager(users);
    }
}


