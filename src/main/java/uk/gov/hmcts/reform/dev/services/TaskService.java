package uk.gov.hmcts.reform.dev.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.dtos.TaskInfo;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    /**
     * Retrieves all tasks from the repository, ordered by creation date descending.
     *
     * @return a list of TaskInfo objects representing all tasks
     */
    public List<TaskInfo> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(TaskInfo::new)
            .toList();
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task to retrieve
     * @return an Optional containing TaskInfo if found, or empty if not found
     */
    public Optional<TaskInfo> getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(TaskInfo::new);
    }

    /**
     * Creates a new task with the provided details.
     *
     * @param title       the title of the task
     * @param description the description of the task
     * @param status      the status of the task
     * @param dueDate     the due date of the task
     * @return the ID of the newly created task
     */
    @Transactional // To rollback DB query if failed execution
    public Long createTask(String title, String description, Status status, LocalDateTime dueDate) {

        var taskToAdd = Task
            .builder()
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Task savedTask = taskRepository.save(taskToAdd);

        return savedTask.getId();
    }

    /**
     * Updates an existing task with the provided details.
     *
     * @param id          the ID of the task to update
     * @param title       the new title of the task
     * @param description the new description of the task
     * @param status      the new status of the task
     * @param dueDate     the new due date of the task
     * @throws TaskNotFoundException if no task with the given ID is found
     */
    @Transactional
    public void updateTask(Long id, String title, String description, Status status, LocalDateTime dueDate) {
        Task taskToUpdate = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        taskToUpdate.setTitle(title);
        taskToUpdate.setDescription(description);
        taskToUpdate.setStatus(status);
        taskToUpdate.setDueDate(dueDate);
        taskToUpdate.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(taskToUpdate);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     * @throws TaskNotFoundException if no task with the given ID is found
     */
    @Transactional
    public void deleteTask(Long id) {
        Task taskToUpdate = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        taskRepository.delete(taskToUpdate);
    }
}

