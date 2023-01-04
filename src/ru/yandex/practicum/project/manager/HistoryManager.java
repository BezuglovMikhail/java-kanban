package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.*;

import java.util.ArrayList;

public interface HistoryManager <T extends Task> {

    void add(T task);
    void remove(int id);

    ArrayList<Task> getHistory();
}
