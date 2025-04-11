package uk.gov.hmcts.reform.dev.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
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

    // Create a task
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        logger.info("[Service] Creating task with title: {}", taskDTO.getTitle());
        validateTaskDTO(taskDTO);
        Task task = mapToEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        logger.info("[Service] Task created successfully with ID: {}", savedTask.getId());
        return mapToDTO(savedTask);
    }

    // Retrieve a task by ID
    public TaskDTO getTaskById(int id) {
        logger.info("[Service] Retrieving task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("[Service] Task not found with ID: {}", id);
                    return new RuntimeException("Task not found with ID: " + id);
                });
        logger.info("[Service] Task retrieved successfully with ID: {}", id);
        return mapToDTO(task);
    }

    // Retrieve all tasks
    public List<TaskDTO> getAllTasks() {
        logger.info("[Service] Retrieving all tasks");
        List<TaskDTO> tasks = taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        logger.info("[Service] Retrieved {} tasks successfully", tasks.size());
        return tasks;
    }

    // Update the status of a task
    @Transactional
    public TaskDTO updateTaskStatus(int id, String status) {
        logger.info("[Service] Updating status of task with ID: {} to {}", id, status);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("[Service] Task not found with ID: {}", id);
                    return new RuntimeException("Task not found with ID: " + id);
                });
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        logger.info("[Service] Task status updated successfully for ID: {}", id);
        return mapToDTO(updatedTask);
    }

    // Delete a task
    @Transactional
    public void deleteTask(int id) {
        logger.info("[Service] Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            logger.error("[Service] Task not found with ID: {}", id);
            throw new RuntimeException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
        logger.info("[Service] Task deleted successfully with ID: {}", id);
    }

    // Validate TaskDTO fields
    private void validateTaskDTO(TaskDTO taskDTO) {
        logger.debug("[Service] Validating TaskDTO: {}", taskDTO);
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            logger.error("[Service] Validation failed: Task title cannot be empty");
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (taskDTO.getStatus() == null || taskDTO.getStatus().isEmpty()) {
            logger.error("[Service] Validation failed: Task status cannot be empty");
            throw new IllegalArgumentException("Task status cannot be empty");
        }
        if (taskDTO.getDueDate() == null) {
            logger.error("[Service] Validation failed: Task due date cannot be null");
            throw new IllegalArgumentException("Task due date cannot be null");
        }
        logger.debug("[Service] Validation passed for TaskDTO: {}", taskDTO);
    }

    // Map TaskDTO to Task entity
    private Task mapToEntity(TaskDTO taskDTO) {
        logger.debug("[Service] Mapping TaskDTO to Task entity: {}", taskDTO);
        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        logger.debug("[Service] Mapped Task entity: {}", task);
        return task;
    }

    // Map Task entity to TaskDTO
    private TaskDTO mapToDTO(Task task) {
        logger.debug("[Service] Mapping Task entity to TaskDTO: {}", task);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setDueDate(task.getDueDate());
        logger.debug("[Service] Mapped TaskDTO: {}", taskDTO);
        return taskDTO;
    }
}