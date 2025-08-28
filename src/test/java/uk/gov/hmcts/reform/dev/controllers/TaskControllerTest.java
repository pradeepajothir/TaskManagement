// src/test/java/uk/gov/hmcts/reform/dev/controllers/TaskControllerTest.java
package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            mockTask(1L, "Title", "Desc", Status.PENDING, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskService.getAllTasks()).thenReturn(List.of(info));

        List<TaskInfo> result = taskController.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Title", result.getFirst().getTitle());
        verify(taskService).getAllTasks();
    }

    @Test
    void getTaskById_returnsTaskInfo() {
        TaskInfo info = new TaskInfo(
            mockTask(2L, "Title2", "Desc2", Status.COMPLETED, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskService.getTaskById(2L)).thenReturn(Optional.of(info));

        ResponseEntity<TaskInfo> response = taskController.getTaskById(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Title2", response.getBody().getTitle());
        verify(taskService).getTaskById(2L);
    }

    @Test
    void getTaskById_returnsNotFound() {
        when(taskService.getTaskById(2L)).thenThrow(TaskNotFoundException.class);

        ResponseEntity<TaskInfo> response = taskController.getTaskById(2L);

        verify(taskService).getTaskById(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTask_returnsNewId() {
        TaskRequest req = new TaskRequest("Title", "Desc", "pending", LocalDateTime.now());
        when(taskService.createTask(anyString(), anyString(), any(Status.class), any(LocalDateTime.class))).thenReturn(10L);

        ResponseEntity<Long> response = taskController.createTask(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody());
        verify(taskService).createTask(anyString(), anyString(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void updateTask_returnsSuccess() {
        TaskRequest req = new TaskRequest("Title", "Desc", "completed", LocalDateTime.now());

        ResponseEntity<String> response = taskController.updateTask(5L, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task updated successfully", response.getBody());
        verify(taskService).updateTask(anyLong(), anyString(), anyString(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void updateTask_returnsNotFound() {

        var dueDate = LocalDateTime.now();
        TaskRequest req = new TaskRequest("Title", "Desc", "completed", dueDate);

        doThrow(TaskNotFoundException.class).when(taskService).updateTask(5L, "Title", "Desc", Status.COMPLETED, dueDate);
        ResponseEntity<String> response = taskController.updateTask(5L, req);

        verify(taskService).updateTask(5L, "Title", "Desc", Status.COMPLETED, dueDate);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteTask_returnsSuccess() {
        ResponseEntity<String> response = taskController.deleteTask(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task deleted successfully", response.getBody());
        verify(taskService).deleteTask(7L);
    }

    // Helper to create a mock Task for TaskInfo constructor
    private uk.gov.hmcts.reform.dev.enities.Task mockTask(Long id, String title, String desc, Status status, LocalDateTime due, LocalDateTime created) {
        uk.gov.hmcts.reform.dev.enities.Task task = mock(uk.gov.hmcts.reform.dev.enities.Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getTitle()).thenReturn(title);
        when(task.getDescription()).thenReturn(desc);
        when(task.getStatus()).thenReturn(status);
        when(task.getDueDate()).thenReturn(due);
        when(task.getCreatedAt()).thenReturn(created);
        return task;
    }
}
