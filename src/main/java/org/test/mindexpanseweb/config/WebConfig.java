package org.test.mindexpanseweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://mind-expanse.vercel.app/")
                        .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS")
                        /*.allowedHeaders("Authorization")
                        .allowedHeaders("Content-Type")
                        .exposedHeaders("Authorization")*/
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };

    }
}
