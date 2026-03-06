package lk.icbt.findit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs all API requests: method, URI, query string, response status, and duration.
 */
@Component
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!log.isInfoEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String query = queryString != null ? "?" + queryString : "";

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            long durationMs = System.currentTimeMillis() - startTime;
            String logLine = String.format("API %s %s%s -> %d (%d ms)", method, uri, query, status, durationMs);
            if (status >= 500) {
                log.error("{}", logLine);
            } else if (status >= 400) {
                log.warn("{}", logLine);
            } else {
                log.info("{}", logLine);
            }
        }
    }
}
