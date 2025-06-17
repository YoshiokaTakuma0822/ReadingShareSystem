package com.readingshare.common.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.parameters.Parameter;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenApiCustomizer pathParameterExampleCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(op -> {
                    if (op.getParameters() == null) {
                        return;
                    }
                    for (Parameter p : op.getParameters()) {
                        if ("path".equals(p.getIn())) {
                            p.setExample("3fa85f64-5717-4562-b3fc-2c963f66afa6");
                        }
                    }
                });
            });
        };
    }
}
