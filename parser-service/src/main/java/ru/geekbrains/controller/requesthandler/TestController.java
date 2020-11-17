package ru.geekbrains.controller.requesthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.geekbrains.common.rest.ResponseMessage;
import ru.geekbrains.model.Task;
import ru.geekbrains.service.requesthandler.RequestHandlerService;

@RequestMapping("/test")
@Controller
public class TestController {


    private RequestHandlerService requestHandlerService;

    @Autowired
    public TestController(RequestHandlerService requestHandlerService) {
        this.requestHandlerService = requestHandlerService;
    }

    @GetMapping
    public ResponseEntity<Object> test() throws Exception {


        Task task = new Task();
        task.setTaskId("1");
        task.setCountry("Россия");
        task.setCity("Москва");
        requestHandlerService.addTask(task);

        return new ResponseEntity<>(new ResponseMessage("ok", "объект добавлен в очередь"), HttpStatus.CREATED);

    }
}
