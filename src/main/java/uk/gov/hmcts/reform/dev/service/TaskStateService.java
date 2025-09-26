package uk.gov.hmcts.reform.dev.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.enums.TaskEvent;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.models.Task;

@Slf4j
@Service
public class TaskStateService {

    private final StateMachineFactory<TaskStatus, TaskEvent> stateMachineFactory;

    public TaskStateService(StateMachineFactory<TaskStatus, TaskEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    public void applyEvent(Task task, TaskEvent event) {
        StateMachine<TaskStatus, TaskEvent> stateMachine = stateMachineFactory.getStateMachine();
        StateMachineContext<TaskStatus, TaskEvent> context =
            new DefaultStateMachineContext<>(task.getStatus(), null, null, null);
        stateMachine.getStateMachineAccessor()
            .doWithAllRegions(access -> access.resetStateMachine(context));
        stateMachine.startReactively().block();
        boolean accepted = stateMachine.sendEvent(event);
        if (!accepted) {
            throw new IllegalStateException(
                "Event " + event + " cannot be applied from state " + task.getStatus()
            );
        }
        TaskStatus newStatus = stateMachine.getState().getId();
        if (!newStatus.equals(task.getStatus())) {
            task.setStatus(newStatus);
        }
    }

}
