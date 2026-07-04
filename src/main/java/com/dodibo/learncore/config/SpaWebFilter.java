package com.dodibo.learncore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SpaWebFilter extends OncePerRequestFilter {

    private static final Pattern FILE_EXTENSION = Pattern.compile(".*\\.[a-zA-Z0-9]{2,8}$");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !"HEAD".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (isBackendPath(path) || path.equals("/") || path.equals("/index.html") || hasFileExtension(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        request.getRequestDispatcher("/index.html").forward(request, response);
    }

    private boolean isBackendPath(String path) {
        return path.startsWith(ApiPaths.BASE + "/")
                || path.equals(ApiPaths.BASE)
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/")
                || path.startsWith("/webjars/");
    }

    private boolean hasFileExtension(String path) {
        int lastSlash = path.lastIndexOf('/');
        String lastSegment = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        return FILE_EXTENSION.matcher(lastSegment).matches();
    }
}
