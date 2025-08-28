package uk.gov.hmcts.reform.dev.models.requests;

import lombok.Builder;

@Builder
public record LoginRequest(String username, String password) {}
