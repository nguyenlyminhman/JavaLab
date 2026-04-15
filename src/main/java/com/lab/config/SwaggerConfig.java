package com.lab.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}") // Thêm giá trị mặc định 8080 nếu không tìm thấy port
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        // 1. Cấu hình danh sách Server
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        Server devServer = new Server()
                .url("https://dev-api.lab.com")
                .description("Dev Environment");

        // 2. Cấu hình Security Scheme (JWT)
        final String securitySchemeName = "bearerAuth";
        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        // 3. Xây dựng OpenAPI object
        return new OpenAPI()
                .info(new Info()
                        .title("JavaLab Project API")
                        .description("Hệ thống Lab học tập Java 21 & Spring Boot 3.x\n\n" +
                                "Các module hiện có: Binance, Concurrency, Lock, Lambda.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Neko Developer")
                                .email("admin@lab.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(localServer, devServer))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                        .addSecuritySchemes(securitySchemeName, bearerAuthScheme)
                );
    }
}

