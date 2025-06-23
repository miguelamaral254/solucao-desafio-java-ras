package br.com.apigestao.infrastructure.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class RequestMdcFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestMdcFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("requestURI", request.getRequestURI());
        MDC.put("method", request.getMethod());
        response.setHeader("X-Request-ID", requestId);

        log.info("Incoming {} {} [requestId={}]",
                request.getMethod(),
                request.getRequestURI(),
                requestId);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            log.info("Completed {} {} → {} ({} ms) [requestId={}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration,
                    requestId);

            MDC.remove("requestId");
            MDC.remove("requestURI");
            MDC.remove("method");
        }
    }
}