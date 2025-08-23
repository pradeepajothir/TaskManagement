package uk.gov.hmcts.reform.dev.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

// Record to encapsulate the data needed to create a new task.
// - Decouples the API layer from the persistence layer.
// - Ensures only necessary fields are exposed for task creation.
// - Immutable and concise representation of the request data.
public record CreateTaskRequest(
    String title,
    String description,
    @JsonProperty("due_date")
    LocalDateTime dueDate
) {}
