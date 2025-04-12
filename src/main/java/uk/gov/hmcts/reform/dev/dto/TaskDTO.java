package uk.gov.hmcts.reform.dev.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a task in the system")
public class TaskDTO {

    @Schema(description = "Unique identifier of the task", example = "1")
    private int id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "Title of the task", example = "Complete documentation")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Write detailed API documentation for the project")
    private String description;

    @NotBlank(message = "Status is mandatory")
    @Schema(description = "Current status of the task", example = "IN_PROGRESS")
    private String status;

    @NotNull(message = "Due date is mandatory")
    @Schema(description = "Due date of the task", example = "2025-04-15T10:00:00")
    private LocalDateTime dueDate;

    @NotNull(message = "Created date is mandatory")
    @Schema(description = "Date and time when the task was created", example = "2025-04-01T10:00:00", readOnly = true)
    private LocalDateTime createdDate;
}