package uk.gov.hmcts.reform.dev.service;

import uk.gov.hmcts.reform.dev.models.TaskDTO;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO getTaskById(Long id);

    List<TaskDTO> getAllTasks();

    TaskDTO updateTaskById(Long id, TaskDTO taskDTO);

    void deleteTaskById(Long id);
}
