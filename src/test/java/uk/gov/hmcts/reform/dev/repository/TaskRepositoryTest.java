package uk.gov.hmcts.reform.dev.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.util.TaskTestDataUtil;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;
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
    void shouldSaveAndFindTask() {
        Task saved = repository.save(sampleTask);
        Optional<Task> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Task");
    }
}
