package com.tometrics.api.services.achievements.config;

import com.google.api.Authentication;
import com.tometrics.api.services.commonservice.JwtService;
import com.tometrics.api.services.commonservice.models.Requester;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final var authHeader = request.getHeader("Authorization");

        if (authHeader ==  null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.substring(7);
        var decodedJWT = jwtService.verifyOrNull(token);
        if (decodedJWT == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var userId = decodedJWT.getClaim("userId").asInt();
        var principal = new Requester(userId);

        var auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                null
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
