package ru.geekbrains.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.dto.MainSettingDto;
import ru.geekbrains.entity.bot.BotData;
import ru.geekbrains.model.RequestParam;
import ru.geekbrains.model.ResponseMessage;
import ru.geekbrains.service.RequestService;
import ru.geekbrains.service.search.SearchService;
import ru.geekbrains.utils.Formatter;

import java.security.Principal;

@RequestMapping("/task")
@RestController
@Slf4j
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final SearchService searchService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseMessage> sendMessage(@RequestBody @NonNull BotData botData) {
        return requestService.create(botData);
    }

    @GetMapping(path = "/completed/{taskId}")
    public ResponseEntity<ResponseMessage> sendNotificationUser(@PathVariable @NonNull String taskId) {
        log.info("get callback with id = " + taskId);
        return requestService.findTaskAndSendResponseToNotificationService(taskId);
    }

    @PostMapping(path = "/create/th")
    public String createTask(@RequestBody MainSettingDto mainSettingDto, Principal principal) {
        RequestParam th = requestService.createTh(mainSettingDto, principal.getName());
        return Formatter.adsToString(searchService.findAdByFilter(th));
    }

}
