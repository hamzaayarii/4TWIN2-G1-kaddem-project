package tn.esprit.spring.kaddem.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    @Test
    void testAddCorsMappings() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration corsRegistration = mock(CorsRegistration.class);
        
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:4200")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(true)).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(3600)).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(corsRegistration).allowedOrigins("http://localhost:4200");
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(corsRegistration).allowedHeaders("*");
        verify(corsRegistration).allowCredentials(true);
        verify(corsRegistration).maxAge(3600);
    }

    @Test
    void testCorsFilter() {
        // Arrange
        WebConfig webConfig = new WebConfig();

        // Act
        CorsFilter corsFilter = webConfig.corsFilter();

        // Assert
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsFilter.getCorsConfigurationSource();
        CorsConfiguration config = source.getCorsConfigurations().get("/**");
        
        assertNotNull(config);
        assertTrue(config.getAllowedOrigins().contains("http://localhost:4200"));
        assertTrue(config.getAllowedHeaders().contains("*"));
        assertTrue(config.getAllowedMethods().contains("*"));
        assertTrue(config.getAllowCredentials());
    }
} 