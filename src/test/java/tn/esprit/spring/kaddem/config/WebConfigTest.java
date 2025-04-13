package tn.esprit.spring.kaddem.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebConfigTest {

    private static final String[] EXPECTED_ORIGINS = {
        "http://localhost:80",
        "http://localhost:4200",
        "http://localhost"
    };

    private static final String[] EXPECTED_METHODS = {
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    };

    @Test
    void testAddCorsMappings() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration corsRegistration = mock(CorsRegistration.class);
        
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(EXPECTED_ORIGINS)).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(EXPECTED_METHODS)).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(3600)).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(corsRegistration).allowedOrigins(EXPECTED_ORIGINS);
        verify(corsRegistration).allowedMethods(EXPECTED_METHODS);
        verify(corsRegistration).allowedHeaders("*");
        verify(corsRegistration).maxAge(3600);
    }

    @Test
    void testCorsFilter() {
        // Arrange
        WebConfig webConfig = new WebConfig();

        // Act
        CorsFilter corsFilter = webConfig.corsFilter();

        // Assert
        assertNotNull(corsFilter, "CorsFilter should not be null");
    }
} 