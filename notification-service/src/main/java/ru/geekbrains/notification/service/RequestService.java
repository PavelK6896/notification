package ru.geekbrains.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.geekbrains.notification.model.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    public List<Notification> notificationList = new ArrayList<>();

    public ResponseEntity<Object> sendMessage(String chatId, String response) {
        notificationList.forEach(notification -> notification.sendMessage(chatId, response));
        return new ResponseEntity<>("ок", HttpStatus.OK);
    }

    public void registr (@NotNull Notification notification){
        notificationList.add(notification);
    }
}
