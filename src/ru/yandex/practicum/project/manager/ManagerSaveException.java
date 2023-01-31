package ru.yandex.practicum.project.manager;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message, IOException e) {
        super(message);
    }

}
