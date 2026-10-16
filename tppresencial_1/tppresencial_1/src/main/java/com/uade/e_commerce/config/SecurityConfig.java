package com.uade.e_commerce.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.uade.e_commerce.service.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Aplica la configuración CORS definida en corsConfigurationSource()
            // para que el navegador permita las peticiones del front (localhost:5173)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/productos").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/categorias").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "VENDEDOR", "USUARIO")
                .requestMatchers(HttpMethod.POST, "/api/usuarios/{usuarioId}/pedidos").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.POST, "/api/usuarios/{usuarioId}/carrito/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{usuarioId}/carrito/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers(HttpMethod.GET, "/api/usuarios/{usuarioId}/carrito/**").hasAnyRole("ADMIN", "USUARIO")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // Crea la configuración CORS que se aplicará a las solicitudes.
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriza solicitudes provenientes del frontend en localhost:5173.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Permite que el frontend use estos métodos HTTP (OPTIONS es el pre-flight).
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Acepta cualquier encabezado enviado en las solicitudes (ej: Authorization).
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        // Permite enviar cookies y credenciales entre distintos orígenes.
        configuration.setAllowCredentials(true);
        // Crea el proveedor que asocia configuraciones CORS con rutas.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a todas las rutas de la aplicación.
        source.registerCorsConfiguration("/**", configuration);
        // Devuelve la configuración CORS para que Spring Security la utilice.
        return source;
    }
}
