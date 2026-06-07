package com.devsu.account.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String DEV_EMAIL = "juanmavelezpa\u0040gmail.com";

    private static final String SERVICE_DESCRIPTION =
            "Microservicio de Cuentas, Movimientos y Reportes (puerto 8082).\n\n"
            + "- Valida clientes contra la proyeccion local cliente_referencia (sync Kafka).\n"
            + "- Movimientos: valor positivo = deposito, negativo = retiro (regla F3: saldo insuficiente -> HTTP 422).\n"
            + "- Reporte F4: GET /api/reportes por rango de fechas y nombre de cliente (case-sensitive, trim espacios).\n"
            + "- Todas las respuestas usan el envelope ApiResponse con correlationId.\n"
            + "- Header opcional X-Correlation-Id para trazabilidad end-to-end.";

    @Bean
    public OpenAPI accountServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Devsu Account Service API")
                        .description(SERVICE_DESCRIPTION)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Juan Manuel Velez Parra")
                                .email(DEV_EMAIL)
                                .url("https://www.linkedin.com/in/juanmavelezdev/")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Host local o Docker (puerto publicado)"),
                        new Server().url("http://account-service:8082").description("Red interna Docker Compose")))
                .components(new Components()
                        .addParameters("CorrelationId", buildCorrelationIdParameter()));
    }

    @Bean
    public OpenApiCustomizer correlationIdHeaderCustomizer() {
        var correlationHeader = buildCorrelationIdParameter();
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> operation.addParametersItem(correlationHeader));
    }

    private static io.swagger.v3.oas.models.parameters.Parameter buildCorrelationIdParameter() {
        return new io.swagger.v3.oas.models.parameters.Parameter()
                .in("header")
                .name("X-Correlation-Id")
                .description("Identificador de trazabilidad. Si no se envia, el servicio genera uno y lo devuelve en la respuesta.")
                .required(false)
                .schema(new StringSchema().example("550e8400-e29b-41d4-a716-446655440000"));
    }
}
