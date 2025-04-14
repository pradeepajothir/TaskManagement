package uk.gov.hmcts.reform.dev.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleNotFoundException() {
        // Simulate a NotFoundException and verify the response
        TaskNotFoundException exception = new TaskNotFoundException("Resource not found");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTaskNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleGenericException() {
        // Simulate a generic exception and verify the response
        Exception exception = new Exception("An error occurred");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    public void testHandleValidationException() {
        // Simulate a ValidationException and verify the response
        ValidationException exception = new ValidationException("Validation failed");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
    }

    @Test
    public void testHandleDatabaseException() {
        DatabaseException exception = new DatabaseException("Database is down", new RuntimeException("Root cause"));
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDatabaseException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("A database error occurred", response.getBody().getMessage());
}

}
