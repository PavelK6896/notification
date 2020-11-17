package ru.geekbrains.notification.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.notification.model.ResponseToNotifier;
import ru.geekbrains.common.rest.ResponseMessage;
import ru.geekbrains.notification.service.RequestService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CallbackController {

    private final RequestService requestService;

    @PostMapping("/callback")
    public ResponseEntity<ResponseMessage> sendNotificationUser(@RequestBody @NonNull ResponseToNotifier response){
        log.info("get response = " + response);
        requestService.sendMessage(response.getBotId(), response.getResponse());
        ResponseMessage message = new ResponseMessage("Уведомление пользователю", String.format("уведомление пользователь с chatID = %s отправлено", response.getBotId()));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
