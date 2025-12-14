package ch.g24.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "https://localhost",
                "http://localhost:*",                        // web dev
                "https://www.g-24.ch",                       // production web
                "https://g24-frontend.ashyhill-9796b5b9.westeurope.azurecontainerapps.io", // Azure frontend
                "capacitor://localhost",                     // Android emulator & device WebView
                "http://10.0.2.2:*",                         // Android emulator mapping to host
                "http://192.168.*.*"                         // real device in LAN (local testing)
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
