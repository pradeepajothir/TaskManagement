package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.exceptions.DatabaseException;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.exceptions.ValidationException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("PENDING");
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedDate(LocalDateTime.now());

        taskDTO = new TaskDTO();
        taskDTO.setId(1);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus("PENDING");
        taskDTO.setDueDate(LocalDateTime.now().plusDays(1));
        taskDTO.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void testCreateTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals(taskDTO.getTitle(), createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_ValidationException_WhenTitleIsEmpty() {
        taskDTO.setTitle("");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            taskService.createTask(taskDTO);
        });

        assertEquals("Task title cannot be empty", exception.getMessage());
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void testGetTaskById_TaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(1);
        });

        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testUpdateTaskStatus_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updatedTask = taskService.updateTaskStatus(1, "COMPLETED");

        assertNotNull(updatedTask);
        assertEquals("COMPLETED", updatedTask.getStatus());
    }

    @Test
    void testUpdateTaskStatus_InvalidStatus() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            taskService.updateTaskStatus(1, "INVALID_STATUS");
        });

        assertTrue(exception.getMessage().contains("Invalid task status"));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1);

        assertDoesNotThrow(() -> taskService.deleteTask(1));

        verify(taskRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        when(taskRepository.existsById(1)).thenReturn(false);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(1);
        });

        assertEquals("Task not found with ID: 1", exception.getMessage());
    }
}
