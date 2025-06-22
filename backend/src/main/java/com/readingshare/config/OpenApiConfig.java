package com.readingshare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI (Swagger) の設定クラス。
 * Bearer Token認証（opaque token）を有効にしてSwagger UIでAPIをテストできるようにする。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reading Share System API")
                        .version("1.0")
                        .description("読書共有システムのAPI仕様書\n\n" +
                                "認証方式: Bearer Token（opaque token）\n" +
                                "ログイン後に取得したトークンをAuthorizationヘッダーに設定してください。"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("opaque")
                .scheme("bearer")
                .description("Bearer認証 - opaqueトークンを使用");
    }
}
