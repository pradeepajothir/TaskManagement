package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.enities.Task;

import java.util.List;
import java.util.Optional;

//JPA Repository safeguards SQL Injection by default
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByOrderByCreatedAtDesc();
    Optional<Task> findTaskById(Long id);
}
