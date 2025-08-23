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
import uk.gov.hmcts.reform.dev.models.requests.TaskRequest;
import uk.gov.hmcts.reform.dev.models.projections.TaskInfo;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.util.List;

import static uk.gov.hmcts.reform.dev.utlis.Sanitizer.sanitize;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskInfo> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    ResponseEntity<TaskInfo> getTaskById(@PathVariable Long id) {
        var taskInfo = taskService.getTaskById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        return ResponseEntity.ok(taskInfo);
    }

    @PostMapping
    public ResponseEntity<Long> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Long newTaskId = taskService.createTask(
            sanitize(taskRequest.title()),
            sanitize(taskRequest.description()),
            taskRequest.dueDate()
        );

        return ResponseEntity.ok(newTaskId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id , @Valid @RequestBody TaskRequest taskRequest) {
        taskService.updateTask(
            id,
            sanitize(taskRequest.title()),
            sanitize(taskRequest.description()),
            taskRequest.dueDate()
        );

        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

}
