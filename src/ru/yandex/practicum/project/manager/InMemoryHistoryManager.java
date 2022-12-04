package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.*;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager <T extends Task> implements HistoryManager {

    public List<T> historyTaskList = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyTaskList.size() < 10) {
            historyTaskList.add((T) task);
        } else if (historyTaskList.size() == 10) {
            historyTaskList.remove(0);
            historyTaskList.add((T) task);
        }
    }

    @Override
    public List<T> getHistory() {
        return historyTaskList;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                ", historyTaskList=" + historyTaskList +
                '}';
    }
}
