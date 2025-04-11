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
        validateTaskDTO(taskDTO);
        logger.info("Creating task with title: {}", taskDTO.getTitle());
        Task task = mapToEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        return mapToDTO(savedTask);
    }

    // Retrieve a task by ID
    public TaskDTO getTaskById(int id) {
        logger.info("Retrieving task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        return mapToDTO(task);
    }

    // Retrieve all tasks
    public List<TaskDTO> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Update the status of a task
    @Transactional
    public TaskDTO updateTaskStatus(int id, String status) {
        logger.info("Updating status of task with ID: {} to {}", id, status);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask);
    }

    // Delete a task
    @Transactional
    public void deleteTask(int id) {
        logger.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Validate TaskDTO fields
    private void validateTaskDTO(TaskDTO taskDTO) {
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (taskDTO.getStatus() == null || taskDTO.getStatus().isEmpty()) {
            throw new IllegalArgumentException("Task status cannot be empty");
        }
        if (taskDTO.getDueDate() == null) {
            throw new IllegalArgumentException("Task due date cannot be null");
        }
    }

    // Map TaskDTO to Task entity
    private Task mapToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        return task;
    }

    // Map Task entity to TaskDTO
    private TaskDTO mapToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setDueDate(task.getDueDate());
        return taskDTO;
    }
}