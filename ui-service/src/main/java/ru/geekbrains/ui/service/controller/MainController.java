package ru.geekbrains.ui.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.geekbrains.ui.service.dto.MainSettingDto;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    @GetMapping
    public String home(Model model) {
        model.addAttribute("info", "main");
        return "main";
    }

    @PostMapping("/main")
    public void main(@ModelAttribute MainSettingDto requestParams) {
        System.out.println(requestParams);
    }

}
