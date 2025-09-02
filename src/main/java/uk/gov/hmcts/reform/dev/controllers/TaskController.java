package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.requests.TaskRequest;
import uk.gov.hmcts.reform.dev.models.dtos.TaskInfo;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.util.List;

import static uk.gov.hmcts.reform.dev.utlis.Sanitizer.sanitize;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Retrieves a list of all tasks.
     *
     * @return a list of {@link TaskInfo} representing all tasks
     */
    @GetMapping
    public List<TaskInfo> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the unique identifier of the task
     * @return a {@link ResponseEntity} containing the {@link TaskInfo} if found, or a 404 Not Found response if not
     */
    @GetMapping("/{id}")
    ResponseEntity<TaskInfo> getTaskById(@PathVariable Long id) {
        try{
            var taskInfo = taskService.getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
            return ResponseEntity.ok(taskInfo);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new task.
     *
     * @param taskRequest the request body containing task details
     * @return a {@link ResponseEntity} containing the ID of the newly created task
     */
    @PostMapping
    public ResponseEntity<Long> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Long newTaskId = taskService.createTask(
            sanitize(taskRequest.title()),
            sanitize(taskRequest.description()),
            Status.getStatus(taskRequest.status()),
            taskRequest.dueDate()
        );

        return ResponseEntity.ok(newTaskId);
    }

    /**
     * Updates an existing task.
     *
     * @param id          the unique identifier of the task to update
     * @param taskRequest the request body containing updated task details
     * @return a {@link ResponseEntity} containing ID of updated task if found, or 404 in not.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id , @Valid @RequestBody TaskRequest taskRequest) {
        try {
            taskService.updateTask(
                id,
                sanitize(taskRequest.title()),
                sanitize(taskRequest.description()),
                Status.getStatus(taskRequest.status()),
                taskRequest.dueDate()
            );
            return ResponseEntity.ok("Task updated successfully");
        } catch (TaskNotFoundException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id the unique identifier of the task to delete
     * @return a {@link ResponseEntity} indicating success or failure of the deletion if task found, else 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
