package com.EcoSoftware.Scrum6.Config;

import com.EcoSoftware.Scrum6.Security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad de Spring
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // Filtro personalizado que valida JWT

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF porque usamos JWT y no sesiones tradicionales
            .csrf(csrf -> csrf.disable())

            // Habilita CORS (Cross-Origin Resource Sharing)
            .cors(cors -> {})

            // Configuración de autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas que no requieren autenticación
                .requestMatchers(
                    "/api/auth/**",        // login / registro de autenticación
                    "/api/personas",       // POST de registro de personas
                    "/api/personas/ping"   // prueba de conexión (ping)
                ).permitAll()

                // Todas las demás rutas requieren autenticación con token
                .anyRequest().authenticated()
            )

            // Configuración de sesiones: sin estado (stateless) porque usamos JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Agrega el filtro JWT antes del filtro de username/password estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean de AuthenticationManager necesario para autenticar usuarios manualmente
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
