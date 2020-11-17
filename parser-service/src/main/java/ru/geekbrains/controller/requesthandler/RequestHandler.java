package ru.geekbrains.controller.requesthandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.common.rest.ResponseMessage;
import ru.geekbrains.model.Task;
import ru.geekbrains.service.requesthandler.RequestHandlerService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestHandler {

    private final RequestHandlerService requestHandlerService;

    @PostMapping("task/create")
    public ResponseEntity<ResponseMessage> addTask(@RequestBody @NonNull Task task) throws Exception {
        return requestHandlerService.addTask(task);
    }
}
