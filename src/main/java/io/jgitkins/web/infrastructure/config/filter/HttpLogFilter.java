package io.jgitkins.web.infrastructure.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class HttpLogFilter extends OncePerRequestFilter {
    private static final List<String> SKIP_PREFIXES = List.of(
            "/css/", "/js/", "/img/", "/svg/", "/favicon", "/webjars/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        log.debug("HTTP Request -> Method: [{}], URI: [{}], ServletPath: [{}], PathInfo: [{}]",
                request.getMethod(), request.getRequestURI(), request.getServletPath(), request.getPathInfo());
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            log.debug("HTTP Response <- Status: [{}], URI: [{}], DurationMs: [{}]",
                    response.getStatus(), request.getRequestURI(), durationMs);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = new UrlPathHelper().getPathWithinApplication(request);
        if (path == null) {
            return false;
        }
        for (String prefix : SKIP_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
