package com.readingshare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI (Swagger) の設定クラス。
 * Bearer Token認証（opaque token）を有効にしてSwagger UIでAPIをテストできるようにする。
 * 
 * 【自動認証の使用方法】
 * 1. ブラウザのlocalStorageに 'auth_token' キーでトークンを保存
 * 2. Swagger UIを開くと自動的にトークンが設定される
 * 3. 手動でAuthorizeボタンからも設定可能
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reading Share System API")
                        .version("1.0")
                        .description("読書共有システムのAPI仕様書\n\n" +
                                "認証方式: Bearer Token（opaque token）\n" +
                                "【自動認証】localStorageの'auth_token'から自動取得\n" +
                                "【手動認証】Authorizeボタンからトークンを手動設定"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("opaque")
                .scheme("bearer")
                .description("Bearer認証 - opaqueトークンを使用\n" +
                           "localStorageの'auth_token'から自動取得されます");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .resourceChain(false);
    }
}
