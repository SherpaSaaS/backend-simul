package org.sherpaengineering.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        corsRegistration.allowedHeaders("Authorization", "Requestor-Type", "Content-Type");
        corsRegistration.exposedHeaders("Content-Disposition"); // file download filename header

        String[] allowedOriginPatternsArray = Stream.of("*" , "localhost:3000" , "localhost:8081")
                .map(String::trim)
                .toArray(String[]::new);

        corsRegistration.allowedOriginPatterns(allowedOriginPatternsArray);
    }
}
