package uk.gov.hmcts.reform.dev.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerFunctionalTest {

    @Value("${server.port:4000}")
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAndRetrieveTask() {
        String taskJson = TaskJsonBuilder.buildValidTaskJson("Functional Test Task", "This is a functional test task", "PENDING");

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("/tasks");

        createResponse.then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("title", equalTo("Functional Test Task"))
            .body("status", equalTo("PENDING"));

        int taskId = createResponse.jsonPath().getInt("id");

        RestAssured.given()
            .get("/tasks/" + taskId)
            .then()
            .statusCode(200)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Functional Test Task"));
    }

    @Test
    void shouldRetrieveAllTasks() {
        RestAssured.given()
            .get("/tasks")
            .then()
            .statusCode(200)
            .body("size()", notNullValue());
    }

    @Test
    void shouldUpdateTaskStatus() {
        String taskJson = TaskJsonBuilder.buildValidTaskJson("Functional Test Task", "This is a functional test task", "PENDING");

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("/tasks");

        int taskId = createResponse.jsonPath().getInt("id");

        String statusUpdateJson = """
            {
                "status": "COMPLETED"
            }
        """;

        RestAssured.given()
            .contentType("application/json")
            .body(statusUpdateJson)
            .patch("/tasks/" + taskId + "/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("COMPLETED"));
    }

    @Test
    void shouldDeleteTask() {
        String taskJson = TaskJsonBuilder.buildValidTaskJson("Functional Test Task", "This is a functional test task", "PENDING");

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("/tasks");

        int taskId = createResponse.jsonPath().getInt("id");

        RestAssured.given()
            .delete("/tasks/" + taskId)
            .then()
            .statusCode(204);

        RestAssured.given()
            .get("/tasks/" + taskId)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestForInvalidTask() {
        String invalidTaskJson = TaskJsonBuilder.buildInvalidTaskJson();

        RestAssured.given()
            .contentType("application/json")
            .body(invalidTaskJson)
            .post("/tasks")
            .then()
            .statusCode(400);
    }

    static class TaskJsonBuilder {

        static String buildValidTaskJson(String title, String description, String status) {
            return """
                {
                    "title": "%s",
                    "description": "%s",
                    "status": "%s",
                    "dueDate": "2025-04-20T10:00:00"
                }
            """.formatted(title, description, status);
        }

        static String buildInvalidTaskJson() {
            return """
                {
                    "title": "",
                    "description": "",
                    "status": "",
                    "dueDate": null
                }
            """;
        }
    }
}