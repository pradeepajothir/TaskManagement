package uk.gov.hmcts.reform.dev.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerFunctionalTest {

    @Test
    void shouldCreateAndRetrieveTask() {
        // Create a new task
        String taskJson = """
            {
                "title": "Functional Test Task",
                "description": "This is a functional test task",
                "status": "PENDING",
                "dueDate": "2025-04-20T10:00:00"
            }
        """;

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("http://localhost:4000/tasks");

        createResponse.then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("title", equalTo("Functional Test Task"))
            .body("status", equalTo("PENDING"));

        // Retrieve the created task
        int taskId = createResponse.jsonPath().getInt("id");

        RestAssured.given()
            .get("http://localhost:4000/tasks/" + taskId)
            .then()
            .statusCode(200)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Functional Test Task"));
    }

    @Test
    void shouldRetrieveAllTasks() {
        RestAssured.given()
            .get("http://localhost:4000/tasks")
            .then()
            .statusCode(200)
            .body("size()", notNullValue());
    }

    @Test
    void shouldUpdateTaskStatus() {
        // Create a new task
        String taskJson = """
            {
                "title": "Functional Test Task",
                "description": "This is a functional test task",
                "status": "PENDING",
                "dueDate": "2025-04-20T10:00:00"
            }
        """;

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("http://localhost:4000/tasks");

        int taskId = createResponse.jsonPath().getInt("id");

        // Update the task status
        String statusUpdateJson = """
            {
                "status": "COMPLETED"
            }
        """;

        RestAssured.given()
            .contentType("application/json")
            .body(statusUpdateJson)
            .patch("http://localhost:4000/tasks/" + taskId + "/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("COMPLETED"));
    }

    @Test
    void shouldDeleteTask() {
        // Create a new task
        String taskJson = """
            {
                "title": "Functional Test Task",
                "description": "This is a functional test task",
                "status": "PENDING",
                "dueDate": "2025-04-20T10:00:00"
            }
        """;

        Response createResponse = RestAssured.given()
            .contentType("application/json")
            .body(taskJson)
            .post("http://localhost:4000/tasks");

        int taskId = createResponse.jsonPath().getInt("id");

        // Delete the task
        RestAssured.given()
            .delete("http://localhost:4000/tasks/" + taskId)
            .then()
            .statusCode(204);

        // Verify the task is deleted
        RestAssured.given()
            .get("http://localhost:4000/tasks/" + taskId)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestForInvalidTask() {
        // Create an invalid task
        String invalidTaskJson = """
            {
                "title": "",
                "description": "",
                "status": "",
                "dueDate": null
            }
        """;

        RestAssured.given()
            .contentType("application/json")
            .body(invalidTaskJson)
            .post("http://localhost:4000/tasks")
            .then()
            .statusCode(400);
    }
}