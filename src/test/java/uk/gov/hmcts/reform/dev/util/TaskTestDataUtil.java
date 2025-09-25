package uk.gov.hmcts.reform.dev.util;

import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import java.time.LocalDateTime;

public final class TaskTestDataUtil {

    private TaskTestDataUtil() {
        // private constructor to prevent instantiation
    }

    public static Task sampleTask() {
        return Task.builder()
            .id(1L)
            .title("Test Task")
            .description("test desc")
            .status(TaskStatus.NEW)
            .dueDate(LocalDateTime.of(2025, 10, 25, 10, 30))
            .build();
    }

    public static TaskDTO sampleTaskDTO() {
        return TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("test desc")
            .status(TaskStatus.NEW)
            .dueDate(LocalDateTime.of(2025, 10, 25, 10, 30))
            .build();
    }

    public static TaskDTO updatedTaskDTO() {
        return TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("test desc")
            .status(TaskStatus.IN_PROGRESS)
            .dueDate(LocalDateTime.of(2025, 10, 25, 10, 30))
            .build();
    }
}
