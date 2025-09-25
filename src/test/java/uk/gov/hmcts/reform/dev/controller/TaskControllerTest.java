package uk.gov.hmcts.reform.dev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.exception.InvalidStatusTransitionException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.service.TaskService;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.dev.util.TaskTestDataUtil;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;
    private Task sampleTask;
    private TaskDTO sampleTaskDTO;
    private TaskDTO updatedTaskDTO;

    @BeforeEach
    void setup() {
        sampleTask = TaskTestDataUtil.sampleTask();
        sampleTaskDTO = TaskTestDataUtil.sampleTaskDTO();
        updatedTaskDTO = TaskTestDataUtil.updatedTaskDTO();
    }

    @Test
    void shouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/hmcts/api/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to test-backend"));
    }

    @Test
    void shouldCreateTask() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(sampleTaskDTO);

        mockMvc.perform(post("/hmcts/api/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Test Task")))
            .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleTaskDTO);

        mockMvc.perform(get("/hmcts/api/task/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Test Task")))
            .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleTaskDTO, sampleTaskDTO));

        mockMvc.perform(get("/hmcts/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldUpdateTaskById() throws Exception {
        when(taskService.updateTaskById(eq(1L), any(TaskDTO.class))).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/hmcts/api/task/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Test Task")))
            .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void shouldReturnInvalidStatusTransition() throws Exception {
        when(taskService.updateTaskById(eq(1L), any(TaskDTO.class)))
            .thenThrow(new InvalidStatusTransitionException(sampleTaskDTO.status(), TaskStatus.NEW));
        mockMvc.perform(put("/hmcts/api/task/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskDTO)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldDeleteTaskById() throws Exception {
        doNothing().when(taskService).deleteTaskById(1L);

        mockMvc.perform(delete("/hmcts/api/task/1"))
            .andExpect(status().isOk());
    }
}

