package com.udea.gpx;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import com.udea.gpx.model.User;

@Configuration
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtRequestFilter jwtRequestFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

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

            logger.debug("🔍 AuthenticationEntryPoint - URI: {}, isAjax: {}, Accept: {}",
                    request.getRequestURI(), isAjaxRequest, accept);

            if (isAjaxRequest) {
                // Para peticiones AJAX/API, devolver 401 JSON
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                String jsonResponse = "{\"error\":\"Unauthorized\",\"message\":\"Token inválido o faltante\",\"status\":401}";
                response.getWriter().write(jsonResponse);
                logger.debug("✅ Devuelto 401 JSON para petición API");
            } else {
                // Para peticiones del navegador, redirigir a OAuth2
                response.sendRedirect("/oauth2/authorization/google");
                logger.debug("✅ Redirigido a OAuth2 para petición del navegador");
            }
        };
    }

    /**
     * Configuración de CSRF selectiva - deshabilita CSRF solo para endpoints de API
     * REST
     * que usan JWT, mantiene protección para OAuth2 y endpoints de sesión.
     */
    private RequestMatcher createCsrfRequestMatcher() {
        // Endpoints que NO necesitan CSRF (API REST con JWT)
        OrRequestMatcher apiEndpoints = new OrRequestMatcher(
                new AntPathRequestMatcher("/api/users/login", "POST"),
                new AntPathRequestMatcher("/api/users/simple-register", "POST"),
                new AntPathRequestMatcher("/api/users", "GET"),
                new AntPathRequestMatcher("/api/users/check-email", "GET"),
                new AntPathRequestMatcher("/api/events/**", "GET"),
                new AntPathRequestMatcher("/api/stages/**", "GET"),
                new AntPathRequestMatcher("/api/stageresults/**", "GET"),
                new AntPathRequestMatcher("/api/categories/**", "GET"),
                new AntPathRequestMatcher("/api/event-categories/**", "GET"),
                new AntPathRequestMatcher("/api/event-vehicles/**", "GET"),
                // Endpoints autenticados con JWT
                new AntPathRequestMatcher("/api/**"));

        // Devolver matcher negado para que CSRF se aplique a todo EXCEPTO endpoints API
        return new NegatedRequestMatcher(apiEndpoints);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // CSRF selectivo - solo para endpoints que usan sesiones (OAuth2)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .requireCsrfProtectionMatcher(createCsrfRequestMatcher())
                        .ignoringRequestMatchers("/api/**") // Deshabilitar CSRF para API REST
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Cambio importante
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
                        // OAuth2 endpoints (mantienen protección CSRF)
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
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.isAdmin();
        }
        return false;
    }
}