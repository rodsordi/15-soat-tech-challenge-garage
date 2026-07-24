package br.com.fiap.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Slf4j
@Configuration
public class SwaggerConfig {

    @Value("${info.app.title}")
    private String appTitle;

    @Value("${info.app.version}")
    private String appVersion;

    @Value("${info.app.description}")
    private String appDescription;

    @Bean
    public OpenAPI openAPI(Info info, Collection<Map.Entry<String, Example>> examples) {
        final String securitySchemeName = "bearerAuth";
        var openApi = new OpenAPI()
                .info(new Info().version(appVersion))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
        examples.forEach(example -> openApi.getComponents()
                .addExamples(example.getKey(), example.getValue()));
        return openApi;
    }

    @Bean
    public Info info() {
        return new Info()
                .title(appTitle)
                .version(appVersion)
                .description(appDescription);
    }
}
