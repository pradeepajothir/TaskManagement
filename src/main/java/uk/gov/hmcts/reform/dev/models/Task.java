package uk.gov.hmcts.reform.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "task")
public class Task {
    @Id
    private Long id;
    private String title;
    private String description;
    @Builder.Default
    private TaskStatus status = TaskStatus.NEW; // Default status
    private LocalDateTime dueDate;
}
