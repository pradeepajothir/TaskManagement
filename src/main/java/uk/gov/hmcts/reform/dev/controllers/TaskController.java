package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.enities.Task;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.projections.TaskInfo;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<TaskInfo> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    ResponseEntity<TaskInfo> getTaskById(@PathVariable Long id) {
        var taskInfo = taskRepository.findTaskById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        return ResponseEntity.ok(taskInfo);
    }

}
