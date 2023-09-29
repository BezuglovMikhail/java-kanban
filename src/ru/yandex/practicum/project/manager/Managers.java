package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.Task;

import java.io.IOException;

public class Managers {

    private static final String URL_REGISTER = "http://localhost:8078/register";

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager(URL_REGISTER);
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
