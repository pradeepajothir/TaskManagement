package uk.gov.hmcts.reform.dev.models;

import java.util.Arrays;

/**
 * Enum representing the status of a task.
 */
public enum Status {
    PENDING("pending"),
    IN_PROGRESS("in progress"),
    COMPLETED("completed");

    private final String id;

    Status(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    public static Status getStatus(String id) {
        return Arrays.stream(Status.values())
            .filter(status -> status.id.equals(id))
            .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
