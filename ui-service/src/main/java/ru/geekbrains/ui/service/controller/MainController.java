package ru.geekbrains.ui.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import ru.geekbrains.ui.service.bean.Token;
import ru.geekbrains.ui.service.dto.MainSettingDto;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    @Value("${db.url}")
    private String urlDb;

    private final Token token;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String main(Model model) {
        model.addAttribute("info", "main");
        return "main";
    }

    @GetMapping("/result")
    public String result(Model model) {
        model.addAttribute("result", token.getResult());
        return "result";
    }

    @PostMapping("/main")
    public String main(@ModelAttribute MainSettingDto requestParams) throws JsonProcessingException {
        System.out.println(requestParams);

        HttpHeaders headers = new HttpHeaders();
        if (token.getToken() != null) {
            headers.set("Authorization", token.getToken());
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestParams), headers);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(urlDb + "/task/create/th", HttpMethod.POST, request, String.class);

        System.out.println(responseEntity.getBody());
        token.setResult(responseEntity.getBody());
        return "redirect:/result";
    }

}
