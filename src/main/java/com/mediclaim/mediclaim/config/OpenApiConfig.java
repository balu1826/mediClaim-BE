package com.mediclaim.mediclaim.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediclaimOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("MediClaim Pro API")
                        .version("1.0")
                        .description(
                                "Healthcare Claims and Policy Management Platform"
                        ));
    }
}