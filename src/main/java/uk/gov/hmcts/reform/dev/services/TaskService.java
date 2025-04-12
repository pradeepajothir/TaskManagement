package uk.gov.hmcts.reform.dev.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.exceptions.DatabaseException;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.exceptions.ValidationException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create a new task with validation
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        logger.info("[TaskService][CREATE TASK] Creating task with title: {}", taskDTO.getTitle());
        validateTaskDTO(taskDTO);
        Task task = mapToEntity(taskDTO);
        try {
            Task savedTask = taskRepository.save(task); // Wrap repository save method
            logger.info("[TaskService][CREATE TASK] Task created successfully with ID: {}", savedTask.getId());
            return mapToDTO(savedTask);
        } catch (Exception ex) {
            logger.error("[TaskService][CREATE TASK] Failed to save task: {}", task, ex);
            throw new DatabaseException("Failed to save task to the database", ex);
        }
    }

    // Retrieve a task by its ID with validation
    public TaskDTO getTaskById(int id) {
        logger.info("[TaskService][GET TASK BY ID] Retrieving task with ID: {}", id);
        validateTaskId(id);
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
            logger.info("[TaskService][GET TASK BY ID] Task retrieved successfully with ID: {}", id);
            return mapToDTO(task);
        } catch (Exception ex) {
            logger.error("[TaskService][GET TASK BY ID] Failed to retrieve task with ID: {}", id, ex);
            throw new DatabaseException("Failed to retrieve task from the database", ex);
        }
    }

    // Retrieve all tasks
    public List<TaskDTO> getAllTasks() {
        logger.info("[TaskService][GET ALL TASKS] Retrieving all tasks");
        try {
            List<TaskDTO> tasks = taskRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            logger.info("[TaskService][GET ALL TASKS] Retrieved {} tasks successfully", tasks.size());
            return tasks;
        } catch (Exception ex) {
            logger.error("[TaskService][GET ALL TASKS] Failed to retrieve tasks", ex);
            throw new DatabaseException("Failed to retrieve tasks from the database", ex);
        }
    }

    // Update the status of a task with validation
    @Transactional
    public TaskDTO updateTaskStatus(int id, String status) {
        logger.info("[TaskService][UPDATE TASK STATUS] Updating status of task with ID: {} to {}", id, status);
        validateTaskId(id);
        validateTaskStatus(status);
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
            task.setStatus(status);
            Task updatedTask = taskRepository.save(task);
            logger.info("[TaskService][UPDATE TASK STATUS] Task status updated successfully for ID: {}", id);
            return mapToDTO(updatedTask);
        } catch (Exception ex) {
            logger.error("[TaskService][UPDATE TASK STATUS] Failed to update task status for ID: {}", id, ex);
            throw new DatabaseException("Failed to update task status in the database", ex);
        }
    }

    // Delete a task by its ID with validation
    @Transactional
    public void deleteTask(int id) {
        logger.info("[TaskService][DELETE TASK] Deleting task with ID: {}", id);
        validateTaskId(id);
        try {
            if (!taskRepository.existsById(id)) {
                throw new TaskNotFoundException("Task not found with ID: " + id);
            }
            taskRepository.deleteById(id);
            logger.info("[TaskService][DELETE TASK] Task deleted successfully with ID: {}", id);
        } catch (Exception ex) {
            logger.error("[TaskService][DELETE TASK] Failed to delete task with ID: {}", id, ex);
            throw new DatabaseException("Failed to delete task from the database", ex);
        }
    }

    // Validate TaskDTO fields
    private void validateTaskDTO(TaskDTO taskDTO) {
        logger.debug("[TaskService][VALIDATE TASK] Validating TaskDTO: {}", taskDTO);
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            logger.error("[TaskService][VALIDATE TASK] Validation failed: Task title cannot be empty");
            throw new ValidationException("Task title cannot be empty");
        }
        if (taskDTO.getStatus() == null || taskDTO.getStatus().isEmpty()) {
            logger.error("[TaskService][VALIDATE TASK] Validation failed: Task status cannot be empty");
            throw new ValidationException("Task status cannot be empty");
        }
        if (taskDTO.getDueDate() == null) {
            logger.error("[TaskService][VALIDATE TASK] Validation failed: Task due date cannot be null");
            throw new ValidationException("Task due date cannot be null");
        }
        logger.debug("[TaskService][VALIDATE TASK] Validation passed for TaskDTO: {}", taskDTO);
    }

    // Validate task ID
    private void validateTaskId(int id) {
        if (id <= 0) {
            logger.error("[TaskService][VALIDATE ID] Validation failed: Task ID must be greater than 0");
            throw new ValidationException("Task ID must be greater than 0");
        }
    }

    // Validate task status
    private void validateTaskStatus(String status) {
        logger.debug("[TaskService][VALIDATE STATUS] Validating task status: {}", status);
        if (status == null || status.isEmpty()) {
            logger.error("[TaskService][VALIDATE STATUS] Validation failed: Task status cannot be empty");
            throw new ValidationException("Task status cannot be empty");
        }
        if (!List.of("PENDING", "IN_PROGRESS", "COMPLETED").contains(status)) {
            logger.error("[TaskService][VALIDATE STATUS] Validation failed: Invalid task status: {}", status);
            throw new ValidationException("Invalid task status: " + status);
        }
        logger.debug("[TaskService][VALIDATE STATUS] Validation passed for status: {}", status);
    }

    // Map TaskDTO to Task entity
    private Task mapToEntity(TaskDTO taskDTO) {
        logger.debug("[TaskService][MAP TO ENTITY] Mapping TaskDTO to Task entity: {}", taskDTO);
        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        logger.debug("[TaskService][MAP TO ENTITY] Mapped Task entity: {}", task);
        return task;
    }

    // Map Task entity to TaskDTO
    private TaskDTO mapToDTO(Task task) {
        logger.debug("[TaskService][MAP TO DTO] Mapping Task entity to TaskDTO: {}", task);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setDueDate(task.getDueDate());
        logger.debug("[TaskService][MAP TO DTO] Mapped TaskDTO: {}", taskDTO);
        return taskDTO;
    }
}