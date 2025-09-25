package uk.gov.hmcts.reform.dev.models;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;

import java.time.LocalDateTime;

@Builder
public record TaskDTO(
    Long id,
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    String title,
    @Size(max = 500, message = "Description must be at most 500 characters")
    String description,
    TaskStatus status,
    @Future(message = "Due date must be in the future")
    LocalDateTime dueDate){
}
