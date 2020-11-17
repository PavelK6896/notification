package ru.geekbrains.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.geekbrains.entity.bot.BotData;
import ru.geekbrains.common.rest.ResponseMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final RestTemplate restTemplate;

    public void send(BotData botData){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8079").path("/task/create");
        String url = builder.build().encode().toUriString();
        log.info(String.format("url = %s", url));
        log.info(String.format("Создаем задачу для парсеров по запросу пользователя = %s, фильтры = %s",
                botData.getUser().getLogin(),
                botData.getAnswer().toString()));
        ResponseEntity responseEntity = restTemplate.postForEntity(url, botData, ResponseMessage.class);
        ResponseMessage responseMessage = (ResponseMessage) responseEntity.getBody();
        log.info(responseMessage.toString());
    }
}
