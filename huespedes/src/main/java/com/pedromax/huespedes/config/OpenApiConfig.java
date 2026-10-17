package com.pedromax.huespedes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(){

        final String schemeName = "bearerAuth";

        return new OpenAPI().info(new Info()
                .title("API de Huespedes")
                .version("1.0.0")
                .description("API para la gestión de las huespedes"))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Servidor del localhost"),
                        new Server().url("http://localhost:8090/api/huespedes").description("Servidor de API Gateway")
                )).components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
