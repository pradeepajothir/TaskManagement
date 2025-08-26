package uk.gov.hmcts.reform.dev.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.enities.Task;

import java.time.LocalDateTime;

/**
 * Projection for {@link uk.gov.hmcts.reform.dev.enities.Task}
 *
 * To hide unnecessary info from repository response.
 */
@Getter
@Setter
public class TaskInfo {
    private Long id;
    private String title;
    private String description;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public TaskInfo(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().toString();
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();
    }
}
