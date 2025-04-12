package uk.gov.hmcts.reform.dev.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}