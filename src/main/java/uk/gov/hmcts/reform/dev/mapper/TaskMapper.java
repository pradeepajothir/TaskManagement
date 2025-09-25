package uk.gov.hmcts.reform.dev.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.dev.enums.TaskEvent;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;

@Component
public class TaskMapper {
    public TaskDTO toDto(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDTO.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .dueDate(task.getDueDate())
            .build();
    }

    public Task toEntity(TaskDTO dto) {
        if (dto == null) {
            return null;
        }

        return Task.builder()
            .id(dto.id())
            .title(dto.title())
            .description(dto.description())
            .status(dto.status() != null ? dto.status() : TaskStatus.NEW)
            .dueDate(dto.dueDate())
            .build();
    }

    public void setTaskStatus(TaskDTO dto, Task task) {
        if (dto == null || task == null) {
            return;
        }
        task.setStatus(dto.status());
    }

    public static TaskEvent statusToEvent(TaskStatus current, TaskStatus target) {
        if (current == TaskStatus.NEW && target == TaskStatus.IN_PROGRESS) {
            return TaskEvent.START;
        }
        if (current == TaskStatus.IN_PROGRESS && target == TaskStatus.COMPLETED) {
            return TaskEvent.COMPLETE;
        }
        if ((current == TaskStatus.NEW || current == TaskStatus.IN_PROGRESS) && target == TaskStatus.CANCELLED) {
            return TaskEvent.CANCEL;
        }
        // Transition not allowed
        return null;
    }
}
