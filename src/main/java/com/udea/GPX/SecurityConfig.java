package com.udea.GPX;

import com.udea.GPX.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException) -> {

            // Detectar si es una petición AJAX/API
            String requestedWith = request.getHeader("X-Requested-With");
            String contentType = request.getHeader("Content-Type");
            String accept = request.getHeader("Accept");
            boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith) ||
                    (contentType != null && contentType.contains("application/json")) ||
                    (accept != null && accept.contains("application/json")) ||
                    request.getRequestURI().startsWith("/api/");

            System.out.println("🔍 AuthenticationEntryPoint - URI: " + request.getRequestURI() +
                    ", isAjax: " + isAjaxRequest + ", Accept: " + accept);

            if (isAjaxRequest) {
                // Para peticiones AJAX/API, devolver 401 JSON
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                String jsonResponse = "{\"error\":\"Unauthorized\",\"message\":\"Token inválido o faltante\",\"status\":401}";
                response.getWriter().write(jsonResponse);
                System.out.println("✅ Devuelto 401 JSON para petición API");
            } else {
                // Para peticiones del navegador, redirigir a OAuth2
                response.sendRedirect("/oauth2/authorization/google");
                System.out.println("✅ Redirigido a OAuth2 para petición del navegador");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/simple-register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/check-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/oauth2/login-url").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stages/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stageresults/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event-categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event-vehicles/participants/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event-vehicles/byevent/**").permitAll()
                        // Admin endpoints (requieren autenticación, pero validación de admin en
                        // controlador)
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/admin").authenticated()
                        // OAuth2 endpoints
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/api/oauth2/login-url").permitAll()
                        .requestMatchers("/api/oauth2/success").permitAll()
                        .requestMatchers("/api/oauth2/profile-status").permitAll()
                        .anyRequest().authenticated())
                // Configuración OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/oauth2/success", true)
                        .failureUrl("/api/oauth2/success?error=oauth2_failed"));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Bean necesario para algunos flujos de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Método utilitario para verificar si el usuario autenticado es admin
    public static boolean isAdmin(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.isAdmin();
        }
        return false;
    }
}