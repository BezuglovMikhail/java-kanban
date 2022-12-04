package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.*;

import java.util.List;

public interface HistoryManager <T extends Task> {

    void add(T task);

    List<Task> getHistory();
}
