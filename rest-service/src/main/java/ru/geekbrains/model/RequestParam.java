package ru.geekbrains.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.geekbrains.entity.bot.Answer;
import ru.geekbrains.entity.user.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParam {
    String taskID;
    User user;
    Answer answer;
    long chatId;
    Date dateCreate;
}
