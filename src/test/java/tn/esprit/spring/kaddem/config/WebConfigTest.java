package tn.esprit.spring.kaddem.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebConfigTest {

    @Test
    void testAddCorsMappings() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration corsRegistration = mock(CorsRegistration.class);
        
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOriginPatterns("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(corsRegistration).allowedOriginPatterns("*");
        verify(corsRegistration).allowedMethods("*");
        verify(corsRegistration).allowedHeaders("*");
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