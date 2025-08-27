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
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public LoginResponse issueToken(String username) {
        // Mock user lookup and returning session token
        log.info("Issue token for user {}", username);
        String token = UUID.randomUUID().toString();
        long expires = Instant.now().getEpochSecond() + TTL_SECONDS;
        sessions.put(token, expires);
        return new LoginResponse(token, expires);
    }

    public boolean isValid(String token) {
        if (token == null) return false;
        Long exp = sessions.get(token);
        if (exp == null) return false;
        boolean valid = exp > Instant.now().getEpochSecond();
        if (!valid) sessions.remove(token);
        return valid;
    }

    public void revoke(String token) {
        sessions.remove(token);
    }
}
