package uk.gov.hmcts.reform.dev.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// Record to encapsulate the data needed to create a new task.
// - Decouples the API layer from the persistence layer.
// - Ensures only necessary fields are exposed for task creation.
// - Immutable and concise representation of the request data.
public record CreateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,
    @Size(max = 500, message = "Description must be at most 500 characters")
    String description,
    @NotNull(message = "Due date is required")
    @JsonProperty("due_date")
    LocalDateTime dueDate
) {}
