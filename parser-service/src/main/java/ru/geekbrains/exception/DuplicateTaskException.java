package ru.geekbrains.exception;

public class DuplicateTaskException extends Exception {
    public DuplicateTaskException(String message) {
        super(message);
    }
}
