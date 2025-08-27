package uk.gov.hmcts.reform.dev.models.responses;

public record LoginResponse(String token, long expiresAtEpochSec) {}

