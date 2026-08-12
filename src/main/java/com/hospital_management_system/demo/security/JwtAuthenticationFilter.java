package com.hospital_management_system.demo.security;

import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.exception.InvalidTokenException;
import com.hospital_management_system.demo.exception.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(jwt);
            String rolStr = jwtUtil.extractRol(jwt);
            Long userId = jwtUtil.extractUserId(jwt);
            Long principalId = jwtUtil.extractPrincipalId(jwt);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtUtil.isTokenValid(jwt, username)) {

                Rol rol;
                try {
                    rol = Rol.valueOf(rolStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role in token: {}", rolStr);
                    filterChain.doFilter(request, response);
                    return;
                }

                HospitalUserDetails userDetails = new HospitalUserDetails(
                        username, userId, principalId, rol
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (TokenExpiredException | InvalidTokenException e) {
            log.debug("Token rejected: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}