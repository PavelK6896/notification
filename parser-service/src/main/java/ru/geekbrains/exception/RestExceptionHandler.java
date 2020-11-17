package ru.geekbrains.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.geekbrains.common.rest.ResponseMessage;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler(DuplicateTaskException.class)
    public ResponseEntity<Object> notFoundExceptionHandler(DuplicateTaskException exception, WebRequest request) {
        ResponseMessage message = new ResponseMessage("Дубликат задачи", exception.getLocalizedMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VariableBlankException.class)
    public ResponseEntity<Object> notFoundExceptionHandler(VariableBlankException exception, WebRequest request) {
        ResponseMessage message = new ResponseMessage("Пустое поле", exception.getLocalizedMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
