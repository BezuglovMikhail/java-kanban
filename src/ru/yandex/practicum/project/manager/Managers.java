package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.Task;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
