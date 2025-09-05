package uk.gov.hmcts.reform.dev.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.hmcts.reform.dev.services.SessionService;

import java.io.IOException;

import static uk.gov.hmcts.reform.dev.utlis.AuthHelper.extractBearer;

/**
 * TokenAuthFilter is a Spring component that intercepts incoming HTTP requests
 * and enforces authentication using bearer tokens. It extends OncePerRequestFilter
 * to ensure filtering occurs only once per request. The filter allows unauthenticated
 * access to specific endpoints (e.g., actuator, login, and CORS preflight OPTIONS requests).
 * For other requests, it checks the Authorization header for a valid bearer token
 * using SessionService. If the token is valid, the request proceeds; otherwise,
 * an HTTP 401 Unauthorized response is returned.
 */
@Component
public class TokenAuthFilter extends OncePerRequestFilter {
    private final SessionService sessions;

    public TokenAuthFilter(SessionService sessions) {
        this.sessions = sessions;
    }

    /**
     * Determines whether the filter should not be applied to the current request.
     * Allows unauthenticated access to actuator endpoints, login endpoint, and CORS preflight OPTIONS requests.
     *
     * @param req the current HTTP request
     * @return true if the request should bypass the filter; false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        // allow unauthenticated endpoints here
        return path.startsWith("/actuator")
            || path.startsWith("/api/auth/login")
            || ("OPTIONS".equalsIgnoreCase(req.getMethod())); // CORS preflight
    }

    /**
     * Filters incoming HTTP requests to enforce bearer token authentication.
     * If the request contains a valid token, it proceeds; otherwise, responds with 401 Unauthorized.
     *
     * @param req   the incoming HTTP request
     * @param res   the HTTP response
     * @param chain the filter chain to pass the request/response to the next filter
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        String token = extractBearer(auth);

        if (token != null && sessions.isValid(token)) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
