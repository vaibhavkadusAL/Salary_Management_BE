package com.acme.salarymanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ACME Employee Salary Management API")
                .version("1.0.0")
                .description("Centralized enterprise REST APIs for managing employee records, compensation histories, and executive dashboard analytics for 10,000+ employees.")
                .contact(new Contact()
                    .name("ACME Engineering & HR Operations")
                    .email("support@acme.org"))
                .license(new License()
                    .name("Proprietary - ACME Organization")
                    .url("https://acme.org/licenses")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Development Server")
            ));
    }
}
