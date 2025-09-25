package uk.gov.hmcts.reform.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.util.TaskTestDataUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;
    @Mock
    private TaskMapper mapper;
    @Mock
    private TaskStateService taskStateService;
    @InjectMocks
    private TaskServiceImpl taskService;
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
    void shouldGetTaskById() {

        when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(mapper.toDto(sampleTask)).thenReturn(sampleTaskDTO);

        TaskDTO result = taskService.getTaskById(1L);

        assertThat(result).isEqualTo(sampleTaskDTO);
    }
}
