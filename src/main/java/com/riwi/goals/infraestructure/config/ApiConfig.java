package com.riwi.goals.infraestructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Goals API",
                version = "1.0",
                description = "API para la gestión de metas financieras, permitiendo a los usuarios establecer y monitorear objetivos financieros.",
                contact = @Contact(
                        name = "Equipo de Desarrollo",
                        email = "soporte@riwi.com",
                        url = "https://riwi.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor local"),
                @Server(url = "pendiente", description = "Servidor en producción")
        }
)
public class ApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Goals API")
                        .version("1.0")
                        .description("API para la gestión de metas financieras.")
                        .termsOfService("pendiente")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Equipo de Desarrollo")
                                .email("soporte@riwi.com")
                                .url("https://riwi.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
