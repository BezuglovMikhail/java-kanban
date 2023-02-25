package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.Task;

import java.io.IOException;

public class Managers {
   /* public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }*/

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078/register");
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
