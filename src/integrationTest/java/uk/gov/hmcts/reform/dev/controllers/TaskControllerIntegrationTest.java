package uk.gov.hmcts.reform.dev.integration.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.hmcts.reform.dev.dto.TaskDTO;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndRetrieveTask() {
        TaskDTO task = buildSampleTask();
        ResponseEntity<TaskDTO> createResponse = restTemplate.postForEntity("/tasks", task, TaskDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getTitle()).isEqualTo(task.getTitle());

        int taskId = createResponse.getBody().getId();
        ResponseEntity<TaskDTO> getResponse = restTemplate.getForEntity("/tasks/" + taskId, TaskDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(taskId);
    }

    @Test
    void shouldRetrieveAllTasks() {
        ResponseEntity<TaskDTO[]> response = restTemplate.getForEntity("/tasks", TaskDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void shouldUpdateTaskStatus() {
        TaskDTO task = buildSampleTask();
        ResponseEntity<TaskDTO> createResponse = restTemplate.postForEntity("/tasks", task, TaskDTO.class);
        int taskId = createResponse.getBody().getId();

        Map<String, String> statusUpdate = Map.of("status", "COMPLETED");
        restTemplate.patchForObject("/tasks/" + taskId + "/status", statusUpdate, Void.class);

        ResponseEntity<TaskDTO> getResponse = restTemplate.getForEntity("/tasks/" + taskId, TaskDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldDeleteTask() {
        TaskDTO task = buildSampleTask();
        ResponseEntity<TaskDTO> createResponse = restTemplate.postForEntity("/tasks", task, TaskDTO.class);
        int taskId = createResponse.getBody().getId();

        restTemplate.delete("/tasks/" + taskId);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/tasks/" + taskId, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentTask() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidTask() {
        TaskDTO invalidTask = new TaskDTO(
            0,
            "",  // Empty title
            "",  // Empty description
            "",  // Invalid status
            null,
            null
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/tasks", invalidTask, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTaskStatus() {
        Map<String, String> statusUpdate = Map.of("status", "COMPLETED");

        restTemplate.patchForObject("/tasks/999999/status", statusUpdate, Void.class);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/tasks/999999", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTask() {
        restTemplate.delete("/tasks/999999");

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/tasks/999999", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private TaskDTO buildSampleTask() {
        return new TaskDTO(
            0,
            "Integration Test Task",
            "This is an integration test task",
            "PENDING",
            LocalDateTime.of(2025, 4, 20, 10, 0),
            null
        );
    }
}
