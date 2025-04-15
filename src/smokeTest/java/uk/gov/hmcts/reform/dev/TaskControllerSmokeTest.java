package uk.gov.hmcts.reform.dev.smokeTest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerSmokeTest {

    @Value("${server.port:4000}")
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldVerifyApplicationIsRunning() {
        RestAssured.given()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        String taskJson = """
            {
                "title": "Smoke Test Task",
                "description": "This is a smoke test task",
                "status": "PENDING",
                "dueDate": "2025-04-20T10:00:00"
            }
        """;

        RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("/tasks")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("title", equalTo("Smoke Test Task"));
    }

    @Test
    void shouldRetrieveAllTasks() {
        RestAssured.given()
            .get("/tasks")
            .then()
            .statusCode(200)
            .body("size()", notNullValue());
    }
}