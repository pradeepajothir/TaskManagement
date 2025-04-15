package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.TaskService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/tasks", produces = "application/json")
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
        summary = "Create a new task",
        description = "Create a new task with the provided details. The `createdDate` field is system-generated and will not be included in the request body."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input provided",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                    }
                """)
            )
        )
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<TaskDTO> createTask(
        @Valid @RequestBody @Parameter(description = "Details of the task to be created") TaskDTO taskDTO
    ) {
        logger.info("[TaskController][CREATE TASK] Received request to create a new task");
        TaskDTO createdTask = taskService.createTask(taskDTO);
        logger.info("[TaskController][CREATE TASK] Task created successfully with ID: {}", createdTask.getId());
        return ResponseEntity.ok(createdTask);
    }

    @Operation(
        summary = "Retrieve a task by ID",
        description = "Retrieve details of a specific task by its unique ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the task"),
        @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "message": "Task not found with the given ID"
                    }
                """)
            )
        )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskDTO> getTaskById(
        @PathVariable @Parameter(description = "ID of the task to retrieve") int id
    ) {
        logger.info("[TaskController][GET TASK BY ID] Received request to retrieve task with ID: {}", id);
        TaskDTO task = taskService.getTaskById(id);
        logger.info("[TaskController][GET TASK BY ID] Task retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(task);
    }

    @Operation(
        summary = "Retrieve all tasks",
        description = "Retrieve a list of all tasks available in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of tasks",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "title": "Sample Task",
                            "description": "This is a sample task",
                            "status": "IN_PROGRESS",
                            "dueDate": "2025-04-15T10:00:00",
                            "createdDate": "2025-04-01T10:00:00"
                        },
                        {
                            "id": 2,
                            "title": "Another Task",
                            "description": "This is another sample task",
                            "status": "PENDING",
                            "dueDate": "2025-04-20T10:00:00",
                            "createdDate": "2025-04-05T10:00:00"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "No tasks found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "message": "No tasks found"
                    }
                """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        logger.info("[TaskController][GET ALL TASKS] Received request to retrieve all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        logger.info("[TaskController][GET ALL TASKS] Retrieved {} tasks successfully", tasks.size());
        return ResponseEntity.ok(tasks); // Always return 200 OK
    }

    @Operation(
        summary = "Update the status of a task",
        description = "Update the status of a specific task by its unique ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "message": "Task not found with the given ID"
                    }
                """)
            )
        )
    })
    @PatchMapping(value = "/{id}/status", consumes = "application/json")
    public ResponseEntity<TaskDTO> updateTaskStatus(
        @PathVariable @Parameter(description = "ID of the task to update") int id,
        @RequestBody @Parameter(description = "JSON body containing the new status of the task") Map<String, String> request
    ) {
        logger.info("[TaskController][UPDATE TASK STATUS] Received request to update status of task with ID: {}", id);
        String status = request.get("status");
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
        logger.info("[TaskController][UPDATE TASK STATUS] Task status updated successfully with ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(
        summary = "Delete a task",
        description = "Delete a specific task by its unique ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteTask(
        @PathVariable @Parameter(description = "ID of the task to delete") int id
    ) {
        logger.info("[TaskController][DELETE TASK] Received request to delete task with ID: {}", id);
        taskService.deleteTask(id);
        logger.info("[TaskController][DELETE TASK] Task deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
