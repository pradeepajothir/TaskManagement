package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.projections.TaskInfo;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Custom query methods can be added here if needed
    List<TaskInfo> findAllByOrderByCreatedAtDesc();
}
