package ru.geekbrains.service.requesthandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.geekbrains.common.rest.ResponseMessage;
import ru.geekbrains.exception.DuplicateTaskException;
import ru.geekbrains.exception.VariableBlankException;
import ru.geekbrains.model.Task;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestHandlerService {

    private final TaskService taskService;

    public ResponseEntity<ResponseMessage> addTask (Task task) throws Exception {
        if(isBlank(task)) {
            throw new VariableBlankException("В объекте пустые поля");
        }
        if(!taskService.add(task)) {
            throw new DuplicateTaskException("Задача находится в очереди");
        }
        return new ResponseEntity<>(new ResponseMessage("ok", "объект добавлен в очередь"), HttpStatus.CREATED);
    }

    public boolean isBlank(Task task){
        return task.getTaskId() == null ||
                task.getCity() == null ||
                task.getCountry() == null ||
                task.getTaskId().equals("") ||
                task.getCountry().equals("") ||
                task.getCity().equals("");
    }
}
