package uk.gov.hmcts.reform.dev.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.enums.TaskEvent;
import uk.gov.hmcts.reform.dev.exception.InvalidStatusTransitionException;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final TaskStateService taskStateService;

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper, TaskStateService taskStateService) {
        this.repository = repository;
        this.mapper = mapper;
        this.taskStateService = taskStateService;
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = mapper.toEntity(taskDTO);
        Task saved = repository.save(task);
        return mapper.toDto(saved);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        return repository.findById(id)
            .map(mapper::toDto)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return repository.findAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }
    @Override
    public TaskDTO updateTaskById(Long id, TaskDTO taskDTO) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        log.info("Update requested: Task ID={}, Current Status={}, Requested Status={}",
                 task.getId(), task.getStatus(), taskDTO.status());
        if (taskDTO.status() != null && !taskDTO.status().equals(task.getStatus())) {
            TaskEvent event = TaskMapper.statusToEvent(task.getStatus(), taskDTO.status());
            if (event != null) {
                taskStateService.applyEvent(task, event);
                log.info("After StateMachine: Task ID={}, New Status={}", task.getId(), task.getStatus());
            } else {
                throw new InvalidStatusTransitionException(task.getStatus(), taskDTO.status());
            }
        } else {
            log.info("No status change required for Task ID={}", task.getId());
        }
        repository.save(task);
        log.info("Update persisted: Task ID={}, Final Status={}", task.getId(), task.getStatus());
        return mapper.toDto(task);
    }


    @Override
    public void deleteTaskById(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
