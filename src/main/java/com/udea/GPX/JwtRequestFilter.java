package com.udea.gpx;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.udea.gpx.model.User;
import com.udea.gpx.service.TokenService;
import com.udea.gpx.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Excluir rutas OAuth2 del filtro JWT
        if (requestPath.startsWith("/oauth2/") || requestPath.startsWith("/login/oauth2/")) {
            chain.doFilter(request, response);
            return;
        }

        // Si ya hay una autenticación OAuth2 válida, no interferir
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.isAuthenticated() &&
                !(existingAuth instanceof AnonymousAuthenticationToken)) {
            // Es una autenticación OAuth2, preservarla
            chain.doFilter(request, response);
            return;
        }

        // Procesamiento JWT normal para el resto de rutas
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String jwt = null;
        Long userId = null;

        // Solo procesar JWT si no hay autenticación previa
        if (authHeader != null && authHeader.startsWith("Bearer ") &&
                (existingAuth == null || !existingAuth.isAuthenticated() ||
                        existingAuth.getPrincipal().equals("anonymousUser"))) {

            jwt = authHeader.substring(7);

            try {
                userId = jwtUtil.extractUserId(jwt);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                // Token inválido, continuar sin autenticación
                // Log security event for monitoring
                log.debug("Token JWT inválido en request: {}", request.getRequestURI());
            }
        }

        if (userId != null &&
                (SecurityContextHolder.getContext().getAuthentication() == null ||
                        !SecurityContextHolder.getContext().getAuthentication().isAuthenticated())) {

            User user = userService.getUserById(userId).orElse(null);

            if (user != null) {
                boolean isTokenValid = jwtUtil.validateToken(jwt);
                boolean isTokenBlacklisted = tokenService.isTokenBlacklisted(jwt);

                if (isTokenValid && !isTokenBlacklisted) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (isTokenBlacklisted) {
                    // Token está en blacklist, enviar unauthorized
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}