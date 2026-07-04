package com.dodibo.learncore.security;

import com.dodibo.learncore.common.tenant.AuthScope;
import com.dodibo.learncore.common.tenant.TenantContext;
import com.dodibo.learncore.handler.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/*
* This class represents the JWT filter in the system.
* The filter is responsible for validating the JWT token.
* The filter is responsible for extracting the user ID and tenant ID from the JWT token.
* The filter is responsible for setting the security context.
* The filter is responsible for clearing the security context.
* */
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /*
    * This method is responsible for validating the JWT token.
    * The method is responsible for extracting the user ID and tenant ID from the JWT token.
    * The method is responsible for setting the security context.
    * The method is responsible for clearing the security context.
    * */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (isPublicAuthPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7);
            Long userId = jwtService.extractUserId(jwt);
            Long tenantId = jwtService.extractTenantId(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!jwtService.isAccessToken(jwt)) {
                    handleJwtException(response, "Invalid token type");
                    return;
                }

                TenantContext.setAuthScope(AuthScope.JWT);
                TenantContext.setTenantId(tenantId);

                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            handleJwtException(response, "Token expired");
        } finally {
            TenantContext.clear();
        }
    }

    /*
    * This method is responsible for handling the JWT exception.
    * The method is responsible for setting the response status.
    * The method is responsible for setting the response content type.
    * The method is responsible for writing the exception response to the response.
    * */
    private boolean isPublicAuthPath(String servletPath) {
        if (!servletPath.contains("/auth/")) {
            return false;
        }
        return !servletPath.endsWith("/change-password");
    }

    private void handleJwtException(HttpServletResponse response, String description) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .businessErrorCode(398)
                .businessErrorDescription(description)
                .error("Error occurred")
                .build();
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionResponse));
    }
}
