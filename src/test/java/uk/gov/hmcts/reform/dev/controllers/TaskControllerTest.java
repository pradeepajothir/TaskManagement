package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.dtos.TaskInfo;
import uk.gov.hmcts.reform.dev.models.requests.TaskRequest;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;
    @InjectMocks
    private TaskController taskController;

    @Test
    void getAllTasks_returnsList() {
        TaskInfo info = new TaskInfo(
            createTask(1L, "Title", "Desc", Status.PENDING, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskService.getAllTasks()).thenReturn(List.of(info));

        var result = taskController.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Title", result.getFirst().getTitle());
        verify(taskService).getAllTasks();
    }

    @Test
    void getTaskById_returnsTaskInfo() {
        TaskInfo info = new TaskInfo(
            createTask(2L, "Title2", "Desc2", Status.COMPLETED, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskService.getTaskById(2L)).thenReturn(Optional.of(info));

        var response = taskController.getTaskById(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Title2", response.getBody().getTitle());
        verify(taskService).getTaskById(2L);
    }

    @Test
    void getTaskById_returnsNotFound() {
        when(taskService.getTaskById(2L)).thenThrow(TaskNotFoundException.class);

        var response = taskController.getTaskById(2L);

        verify(taskService).getTaskById(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTask_returnsNewId() {
        TaskRequest req = new TaskRequest("Title", "Desc", "pending", LocalDateTime.now());
        when(taskService.createTask(anyString(), anyString(), any(Status.class), any(LocalDateTime.class))).thenReturn(10L);

        var response = taskController.createTask(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody());
        verify(taskService).createTask(anyString(), anyString(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void updateTask_returnsSuccess() {
        TaskRequest req = new TaskRequest("Title", "Desc", "completed", LocalDateTime.now());

        var response = taskController.updateTask(5L, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task updated successfully", response.getBody());
        verify(taskService).updateTask(anyLong(), anyString(), anyString(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void updateTask_returnsNotFound() {

        var dueDate = LocalDateTime.now();
        TaskRequest req = new TaskRequest("Title", "Desc", "completed", dueDate);

        doThrow(TaskNotFoundException.class).when(taskService).updateTask(5L, "Title", "Desc", Status.COMPLETED, dueDate);
        var response = taskController.updateTask(5L, req);

        verify(taskService).updateTask(5L, "Title", "Desc", Status.COMPLETED, dueDate);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteTask_returnsSuccess() {
        var response = taskController.deleteTask(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task deleted successfully", response.getBody());
        verify(taskService).deleteTask(7L);
    }

    // Helper to creaye tasks
    private Task createTask(Long id, String title, String desc, Status status, LocalDateTime due, LocalDateTime created) {
        return Task
            .builder()
            .id(id)
            .title(title)
            .description(desc)
            .status(status)
            .dueDate(due)
            .createdAt(created)
            .build();
    }
}
