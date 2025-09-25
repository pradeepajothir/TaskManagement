package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.util.List;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/hmcts/api")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ok("Welcome to test-backend");
    }

    @PostMapping("/task")
    @Operation(summary = "Create a task")
    @ApiResponse(responseCode = "200", description = "Found the employee")
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);

    }

    @GetMapping("/task/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", description = "Found the employee")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);

    }

    @GetMapping("/tasks")
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Found the employee")
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();

    }

    @PutMapping("/task/{id}")
    @Operation(summary = "Update a task based on ID")
    @ApiResponse(responseCode = "200", description = "Found the employee")
    public TaskDTO updateTaskById(@PathVariable Long id,@Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTaskById(id, taskDTO);

    }

    @DeleteMapping("/task/{id}")
    @Operation(summary = "Delete a task based on ID")
    @ApiResponse(responseCode = "200", description = "Found the employee")
    public void deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}
