package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.models.requests.LoginRequest;
import uk.gov.hmcts.reform.dev.models.responses.LoginResponse;
import uk.gov.hmcts.reform.dev.services.SessionService;

import static uk.gov.hmcts.reform.dev.utlis.AuthHelper.extractBearer;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionService sessions;

    public AuthController(SessionService sessions) {
        this.sessions = sessions;
    }

    /**
     * Handles mock user login requests.
     * Accepts a {@link LoginRequest} containing username and password.
     * Returns a {@link LoginResponse} with a token if credentials are non-empty,
     * otherwise responds with HTTP 400 Bad Request.
     *
     * @param req the login request containing username and password
     * @return ResponseEntity with LoginResponse if successful, or Bad Request if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        // MOCK: accept any non-empty credentials
        if (req.username() == null || req.username().isBlank()
            || req.password() == null || req.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sessions.issueToken(req.username()));
    }

    /**
     * Handles user logout requests.
     * Expects an optional `Authorization` header containing a bearer token.
     * If a valid token is provided, it will be revoked.
     * Always responds with HTTP 204 No Content.
     *
     * @param auth the `Authorization` header value, may be null
     * @return ResponseEntity with no content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name="Authorization", required=false) String auth) {
        String token = extractBearer(auth);
        if (token != null) sessions.revoke(token);
        return ResponseEntity.noContent().build();
    }
}
