package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.dtos.TaskInfo;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_returnsMappedTaskInfoList() {
        Task task = Task.builder()
            .id(1L)
            .title("Test")
            .description("Desc")
            .status(Status.PENDING)
            .dueDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        when(taskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(task));

        List<TaskInfo> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test", result.getFirst().getTitle());
        verify(taskRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getTaskById_returnsMappedTaskInfo() {
        Task task = Task.builder()
            .id(2L)
            .title("Title")
            .description("Desc")
            .status(Status.COMPLETED)
            .dueDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        Optional<TaskInfo> result = taskService.getTaskById(2L);

        assertTrue(result.isPresent());
        assertEquals("Title", result.get().getTitle());
        verify(taskRepository).findById(2L);
    }

    @Test
    void getTaskById_returnsEmptyIfNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TaskInfo> result = taskService.getTaskById(99L);

        assertFalse(result.isPresent());
        verify(taskRepository).findById(99L);
    }

    @Test
    void createTask_savesAndReturnsId() {
        Task task = Task.builder()
            .id(3L)
            .title("New")
            .description("Desc")
            .status(Status.IN_PROGRESS)
            .dueDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Long id = taskService.createTask("New", "Desc", Status.IN_PROGRESS, task.getDueDate());

        assertEquals(3L, id);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertEquals("New", captor.getValue().getTitle());
    }

    @Test
    void updateTask_updatesAndSavesTask() {
        Task task = Task.builder()
            .id(4L)
            .title("Old")
            .description("OldDesc")
            .status(Status.PENDING)
            .dueDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        when(taskRepository.findById(4L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(4L, "Updated", "NewDesc", Status.COMPLETED, task.getDueDate());

        assertEquals("Updated", task.getTitle());
        assertEquals("NewDesc", task.getDescription());
        assertEquals(Status.COMPLETED, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_throwsIfNotFound() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                     () -> taskService.updateTask(5L, "Title", "Desc", Status.PENDING, LocalDateTime.now()));
        verify(taskRepository).findById(5L);
    }

    @Test
    void deleteTask_deletesTask() {
        Task task = Task.builder()
            .id(6L)
            .title("Delete")
            .description("Desc")
            .status(Status.PENDING)
            .dueDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        when(taskRepository.findById(6L)).thenReturn(Optional.of(task));

        taskService.deleteTask(6L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_throwsIfNotFound() {
        when(taskRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(7L));
        verify(taskRepository).findById(7L);
    }
}
