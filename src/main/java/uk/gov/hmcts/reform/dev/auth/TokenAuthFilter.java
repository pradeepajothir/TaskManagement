package uk.gov.hmcts.reform.dev.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.hmcts.reform.dev.controllers.AuthController;
import uk.gov.hmcts.reform.dev.services.SessionService;

import java.io.IOException;

import static uk.gov.hmcts.reform.dev.utlis.AuthHelper.extractBearer;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {
    private final SessionService sessions;

    public TokenAuthFilter(SessionService sessions) {
        this.sessions = sessions;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        // allow unauthenticated endpoints here
        return path.startsWith("/actuator")
            || path.startsWith("/api/auth/login")
            || ("OPTIONS".equalsIgnoreCase(req.getMethod())); // CORS preflight
    }

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
