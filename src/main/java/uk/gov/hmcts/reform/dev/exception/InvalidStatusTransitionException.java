package uk.gov.hmcts.reform.dev.exception;

import lombok.Getter;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;

@Getter
public class InvalidStatusTransitionException extends RuntimeException {
    private final TaskStatus from;
    private final TaskStatus to;

    public InvalidStatusTransitionException(TaskStatus from, TaskStatus to) {
        super("Cannot change status from " + from + " to " + to);
        this.from = from;
        this.to = to;
    }

}
