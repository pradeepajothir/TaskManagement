package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.models.projections.TaskInfo;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<TaskInfo> findAllByOrderByCreatedAtDesc();
    Optional<TaskInfo> findTaskById(Long id);
}
