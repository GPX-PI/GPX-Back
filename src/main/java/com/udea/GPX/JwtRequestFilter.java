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

        // Excluir rutas OAuth2 del filtro JWT
        if (shouldExcludeFromJwtProcessing(request)) {
            chain.doFilter(request, response);
            return;
        }

        // Si ya hay una autenticación OAuth2 válida, no interferir
        if (hasValidExistingAuthentication()) {
            chain.doFilter(request, response);
            return;
        } // Procesamiento JWT normal para el resto de rutas
        String jwt = extractJwtFromRequest(request);
        if (jwt != null) {
            TokenProcessingResult result = processJwtToken(jwt, request, response);
            if (result.shouldReturn()) {
                return; // Si hay error crítico (token expirado/blacklisted), no continuar
            }
        }

        chain.doFilter(request, response);
    }

    private TokenProcessingResult processJwtToken(String jwt, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Long userId = jwtUtil.extractUserId(jwt);
            if (userId != null && authenticateUserWithJwt(userId, jwt, request, response)) {
                return TokenProcessingResult.stopProcessing(); // Token blacklisted, response status ya fue seteado
            }
            return TokenProcessingResult.continueProcessing();
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return TokenProcessingResult.stopProcessing();
        } catch (Exception e) {
            // Token inválido, continuar sin autenticación
            log.debug("Token JWT inválido en request");
            return TokenProcessingResult.continueProcessing();
        }
    }

    private static class TokenProcessingResult {
        private final boolean shouldReturn;

        private TokenProcessingResult(boolean shouldReturn) {
            this.shouldReturn = shouldReturn;
        }

        public boolean shouldReturn() {
            return shouldReturn;
        }

        public static TokenProcessingResult stopProcessing() {
            return new TokenProcessingResult(true);
        }

        public static TokenProcessingResult continueProcessing() {
            return new TokenProcessingResult(false);
        }
    }

    private boolean shouldExcludeFromJwtProcessing(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        return requestPath.startsWith("/oauth2/") || requestPath.startsWith("/login/oauth2/");
    }

    private boolean hasValidExistingAuthentication() {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return existingAuth != null && existingAuth.isAuthenticated() &&
                !(existingAuth instanceof AnonymousAuthenticationToken);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (authHeader != null && authHeader.startsWith("Bearer ") &&
                (existingAuth == null || !existingAuth.isAuthenticated() ||
                        existingAuth.getPrincipal().equals("anonymousUser"))) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean authenticateUserWithJwt(Long userId, String jwt, HttpServletRequest request,
            HttpServletResponse response) {
        if (!shouldAuthenticateUser()) {
            return false;
        }

        User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        return processUserAuthentication(user, jwt, request, response);
    }

    private boolean shouldAuthenticateUser() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        return currentAuth == null || !currentAuth.isAuthenticated();
    }

    private boolean processUserAuthentication(User user, String jwt, HttpServletRequest request,
            HttpServletResponse response) {
        boolean isTokenValid = jwtUtil.validateToken(jwt);
        boolean isTokenBlacklisted = tokenService.isTokenBlacklisted(jwt);

        if (isTokenValid && !isTokenBlacklisted) {
            setAuthenticationInContext(user, request);
            return false;
        } else if (isTokenBlacklisted) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return true; // Indica que se envió respuesta de error
        }
        return false;
    }

    private void setAuthenticationInContext(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, Collections.emptyList());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}