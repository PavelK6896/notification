package ru.geekbrains.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
    String title;
    String Details;
    String date;

    public ResponseMessage(String title, String details) {
        this.title = title;
        Details = details;
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
