package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Order(1)
    @Bean
    RouterFunction<ServerResponse> apiRoute() {
        return route()
                .before(BeforeFilterFunctions.uri("http://localhost:8080"))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .before(BeforeFilterFunctions.rewritePath("/api/", "/"))
                .GET("/api/**", http())
                .build();
    }

    @Order(2)
    @Bean
    RouterFunction<ServerResponse> cdnRoute() {
        return route()
                .before(BeforeFilterFunctions.uri("http://localhost:8020"))
                .GET("/**", http())
                .build();
    }

}
