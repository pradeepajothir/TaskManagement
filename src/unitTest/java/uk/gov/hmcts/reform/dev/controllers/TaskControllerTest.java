package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private TaskDTO getSampleTask() {
        return TaskDTO.builder()
            .id(1)
            .title("Test Task")
            .description("Test Description")
            .status("IN_PROGRESS")
            .dueDate(LocalDateTime.now().plusDays(5))
            .createdDate(LocalDateTime.now())
            .build();
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskDTO task = getSampleTask();
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task.getId()))
            .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    void shouldGetTaskByIdSuccessfully() throws Exception {
        TaskDTO task = getSampleTask();
        when(taskService.getTaskById(1)).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task.getId()))
            .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    void shouldGetAllTasksSuccessfully() throws Exception {
        TaskDTO task = getSampleTask();
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(task.getId()));
    }

    @Test
    void shouldReturn404WhenNoTasksFound() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        TaskDTO updatedTask = getSampleTask();
        updatedTask.setStatus("COMPLETED");

        when(taskService.updateTaskStatus(eq(1), eq("COMPLETED"))).thenReturn(updatedTask);

        mockMvc.perform(patch("/tasks/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "COMPLETED"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isNoContent());
    }
}
