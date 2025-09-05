package uk.gov.hmcts.reform.dev.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.responses.LoginResponse;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SessionService {
    private static final long TTL_SECONDS = 60 * 60; // 1 hour
    /**
     * List of session tokens with their expiration times in thread-safe map
     * within thread-shared singleton service.
     * (In production, consider using a distributed cache like Redis).
     */
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    /**
     * Issues a new session token for the specified username.
     * The token is stored with an expiration time (TTL).
     *
     * @param username the username for which to issue the token
     * @return a {@link LoginResponse} containing the token and its expiration time
     */
    public LoginResponse issueToken(String username) {
        // Mock user lookup and returning session token
        log.info("Issue token for user {}", username);
        String token = UUID.randomUUID().toString();
        long expires = Instant.now().getEpochSecond() + TTL_SECONDS;
        sessions.put(token, expires);
        return new LoginResponse(token, expires);
    }

    /**
     * Checks if the provided session token is valid.
     * A token is considered valid if it exists and has not expired.
     * If the token is expired, it is removed from the session store.
     *
     * @param token the session token to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean isValid(String token) {
        if (token == null) {
            return false;
        }
        Long exp = sessions.get(token);

        if (exp == null) {
            return false;
        }
        boolean valid = exp > Instant.now().getEpochSecond();
        if (!valid) {
            sessions.remove(token);
        }
        return valid;
    }

    /**
     * Revokes the specified session token, removing it from the session store.
     *
     * @param token the session token to revoke
     */
    public void revoke(String token) {
        sessions.remove(token);
    }
}
