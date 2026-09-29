package music.license.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI licenciaPlusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Licencia+ API")
                        .version("1.0.0")
                        .description("Marketplace de licenciamiento de beats con gestión de créditos colaborativos. "
                                + "Autenticación con JWT: regístrate en /api/auth/register, haz login en "
                                + "/api/auth/login y envía el token como header Authorization: Bearer <token>."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
