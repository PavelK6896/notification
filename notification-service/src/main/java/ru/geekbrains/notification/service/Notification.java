package ru.geekbrains.notification.service;

public interface Notification {
    void sendMessage(String chatId, String response);
}
