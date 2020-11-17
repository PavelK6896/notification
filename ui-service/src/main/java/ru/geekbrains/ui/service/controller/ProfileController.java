package ru.geekbrains.ui.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.geekbrains.ui.service.bean.Token;
import ru.geekbrains.ui.service.validation.ProfileUser;

import java.util.Objects;

@Controller
@RequestMapping("profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinderAuth) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinderAuth.registerCustomEditor(String.class, stringTrimmerEditor); // set message error
    }

    @Value("${db.url}")
    private String urlDb;

    private final Token token;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String addProfile(Model model) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        ProfileUser profileUser = null;
        if (token.getToken() != null) {
            headers.set("Authorization", token.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate
                    .exchange(urlDb + "/auth/profile", HttpMethod.GET, request, String.class);
            profileUser = objectMapper.readValue(Objects.requireNonNull(responseEntity.getBody()), ProfileUser.class);
            log.info("профиль получен " + profileUser);
        }
        if (profileUser == null) {
            profileUser = new ProfileUser();
        }
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("info", "set profile");
        return "auth/profile";
    }

    @PostMapping("/process")
    public String processProfileForm(
            @ModelAttribute("profileUser") @Validated ProfileUser profileUser,
            BindingResult bindingResult,
            Model model) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            return "auth/profile";
        }
        HttpHeaders headers = new HttpHeaders();
        if (token.getToken() != null) {
            headers.set("Authorization", token.getToken());
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(profileUser), headers);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(urlDb + "/auth/profile", HttpMethod.POST, request, String.class);
        return "redirect:/home";
    }


}
