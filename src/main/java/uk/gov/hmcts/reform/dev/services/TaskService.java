package uk.gov.hmcts.reform.dev.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.models.projections.TaskInfo;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Optional<TaskInfo> getTaskById(Long id) {
        return taskRepository.findTaskById(id);
    }

    public List<TaskInfo> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional // To rollback DB query if failed execution
    public Long createTask(String title, String description, LocalDateTime dueDate) {

        var taskToAdd = Task
            .builder()
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

       Task savedTask = taskRepository.save(taskToAdd);

        return savedTask.getId();
    }
}

