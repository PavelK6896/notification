package ru.geekbrains.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.dto.JwtRequestToken;
import ru.geekbrains.dto.JwtResponseToken;
import ru.geekbrains.dto.ProfileUserDto;
import ru.geekbrains.dto.RegisterUserDto;
import ru.geekbrains.entity.user.PersonalData;
import ru.geekbrains.jwt.JwtTokenProvider;
import ru.geekbrains.service.UserDetailsServiceImplements;
import ru.geekbrains.service.UserService;

import java.security.Principal;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final UserDetailsServiceImplements userDetailsServiceImplements;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequestToken jwtRequestToken) throws JsonProcessingException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequestToken.getLogin(), jwtRequestToken.getPassword()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(403).body("неверный логин или пароль");
        }
        UserDetails userDetails = userDetailsServiceImplements.loadUserByUsername(jwtRequestToken.getLogin());
        JwtResponseToken jwtResponseToken = new JwtResponseToken(jwtTokenProvider.generateToken(userDetails));
        return ResponseEntity.ok(objectMapper.writeValueAsString(jwtResponseToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> login(@RequestBody RegisterUserDto registerUserDto) {
        userService.save(registerUserDto);
        return ResponseEntity.ok("зарегистрироан");
    }

    @PostMapping("/profile")
    public ResponseEntity<?> profile(@RequestBody ProfileUserDto profileUserDto, Principal principal) {
        userService.savePersonalData(principal.getName(), profileUserDto);
        return ResponseEntity.ok("профиль обнавлен");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) throws JsonProcessingException {
        PersonalData personalData = userService.getPersonalData(principal.getName());
        log.info("профиль отправлен " + personalData);
        return ResponseEntity.ok(objectMapper.writeValueAsString(personalData));
    }

}
