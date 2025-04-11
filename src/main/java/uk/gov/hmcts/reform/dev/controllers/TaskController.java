package uk.gov.hmcts.reform.dev.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.TaskService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/tasks", produces = "application/json")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create a task
    @PostMapping(consumes = "application/json")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        logger.info("[Controller] Received request to create task with title: {}", taskDTO.getTitle());
        TaskDTO createdTask = taskService.createTask(taskDTO);
        logger.info("[Controller] Task created successfully with ID: {}", createdTask.getId());
        return ResponseEntity.ok(createdTask);
    }

    // Retrieve a task by ID
    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable int id) {
        logger.info("[Controller] Received request to retrieve task with ID: {}", id);
        TaskDTO task = taskService.getTaskById(id);
        logger.info("[Controller] Task retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(task);
    }

    // Retrieve all tasks
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        logger.info("[Controller] Received request to retrieve all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        logger.info("[Controller] Retrieved {} tasks successfully", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    // Update the status of a task
    @PatchMapping(value = "/{id}/status", consumes = "application/json")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable int id, @RequestParam String status) {
        logger.info("[Controller] Received request to update status of task with ID: {} to {}", id, status);
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
        logger.info("[Controller] Task status updated successfully for ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        logger.info("[Controller] Received request to delete task with ID: {}", id);
        taskService.deleteTask(id);
        logger.info("[Controller] Task deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}