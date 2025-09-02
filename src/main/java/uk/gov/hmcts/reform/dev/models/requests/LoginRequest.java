package uk.gov.hmcts.reform.dev.models.requests;

import lombok.Builder;

/**
 * Request model for login.
 */
@Builder
public record LoginRequest(String username, String password) {}
