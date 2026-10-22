package co.edu.sena.pae.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion global de CORS: permite que el front-end (React) consuma la API.
 * El origen permitido se define en application.properties (app.cors.origen).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.origen:http://localhost:5173}")
    private String origenPermitido;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenPermitido)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
