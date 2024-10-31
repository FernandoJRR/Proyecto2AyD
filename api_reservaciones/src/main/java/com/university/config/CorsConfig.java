package com.university.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.university.models.noBD.AppProperties;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private AppProperties appProperties;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:" + this.appProperties.getHostFront1(),
                        "http://localhost:" + this.appProperties.getHostFront2())
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH")
                .allowedHeaders("*") // Permite todos los headers
                .allowCredentials(true);
    }
}
