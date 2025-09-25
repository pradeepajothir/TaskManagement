package uk.gov.hmcts.reform.dev.models;

public record InvalidStatusTransitionResponse(
       String error,
       String message,
       String fromStatus,
       String toStatus) {
}
