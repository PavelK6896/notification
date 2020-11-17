package ru.geekbrains.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entity.bot.Answer;
import ru.geekbrains.model.RequestParam;
import ru.geekbrains.service.search.SearchService;
import ru.geekbrains.utils.Formatter;


@RequestMapping("/search")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public String searchAllAdByRequestParam(@RequestBody @NonNull RequestParam requestParam) {
        log.info(String.format("get test request param = %s", requestParam));
        return Formatter.adsToString(searchService.findAdByFilter(requestParam));
    }

    @GetMapping
    public RequestParam getTestRequestParam() {
        RequestParam requestParam = new RequestParam();
        Answer answer = new Answer();
        answer.setId(1);
        answer.setCountry("Россия");
        answer.setCity("Москва");
        answer.setRooms("1,2");
        answer.setMinPrice(13);
        answer.setMaxPrice(20);
        answer.setFloor("1");
        requestParam.setAnswer(answer);
        return requestParam;
    }


}
