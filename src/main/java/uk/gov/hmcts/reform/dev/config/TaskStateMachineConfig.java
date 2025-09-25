package uk.gov.hmcts.reform.dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import uk.gov.hmcts.reform.dev.enums.TaskEvent;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class TaskStateMachineConfig extends EnumStateMachineConfigurerAdapter<TaskStatus, TaskEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<TaskStatus, TaskEvent> states) throws Exception {
        states.withStates()
            .initial(TaskStatus.NEW)
            .states(EnumSet.allOf(TaskStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TaskStatus, TaskEvent> transitions) throws Exception {
        transitions
            .withExternal()
            .source(TaskStatus.NEW)
            .target(TaskStatus.IN_PROGRESS)
            .event(TaskEvent.START)
            .and()

            .withExternal()
            .source(TaskStatus.IN_PROGRESS)
            .target(TaskStatus.COMPLETED)
            .event(TaskEvent.COMPLETE)
            .and()

            .withExternal()
            .source(TaskStatus.NEW)
            .target(TaskStatus.CANCELLED)
            .event(TaskEvent.CANCEL)
            .and()

            .withExternal()
            .source(TaskStatus.IN_PROGRESS)
            .target(TaskStatus.CANCELLED)
            .event(TaskEvent.CANCEL);
    }
}
