package ru.geekbrains.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.geekbrains.dto.ProfileUserDto;
import ru.geekbrains.dto.RegisterUserDto;
import ru.geekbrains.entity.user.PersonalData;
import ru.geekbrains.entity.user.Role;
import ru.geekbrains.entity.user.Status;
import ru.geekbrains.entity.user.User;
import ru.geekbrains.repository.RoleRepository;
import ru.geekbrains.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public void save(RegisterUserDto registerUserDto) {
        User userNew = new User();
        userNew.setLogin(registerUserDto.getLogin());
        userNew.setName(registerUserDto.getName());
        userNew.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        userNew.setStatus(Status.ACTIVE);
        Optional<Role> byId = roleRepository.findById(2l);
        List<Role> list = new ArrayList();
        list.add(byId.get());
        userNew.setRoles(list);
        userRepository.save(userNew);
    }

    public void savePersonalData(String login, ProfileUserDto profileUserDto) {
        User user = userRepository.findByLogin(login).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        PersonalData personalData;
        if ((personalData = user.getPersonalData()) == null){
            personalData = new PersonalData();
        }
        personalData.setWhatsApp(profileUserDto.getWhatsApp());
        personalData.setEmail(profileUserDto.getEmail());
        personalData.setFacebook(profileUserDto.getFacebook());
        personalData.setTelegram(profileUserDto.getTelegram());
        user.setPersonalData(personalData);
        User save = userRepository.save(user);
        log.info("профиль добавлен " + save.getPersonalData());
    }

    public PersonalData getPersonalData(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        return user.getPersonalData();
    }
}
