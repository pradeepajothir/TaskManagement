package uk.gov.hmcts.reform.dev.models.responses;

import lombok.Builder;

@Builder
public record LoginResponse(String token, long expiresAtEpochSec) {}

