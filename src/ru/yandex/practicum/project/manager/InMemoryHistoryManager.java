package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.*;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager<Task> {

    private final LinkedList<Task> historyTaskList = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyTaskList.size() < 10) {
            historyTaskList.add(task);
        } else if (historyTaskList.size() == 10) {
            historyTaskList.removeFirst();
            historyTaskList.add(task);
        }
    }

    @Override
    public LinkedList<Task> getHistory() {
        return historyTaskList;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                ", historyTaskList=" + historyTaskList +
                '}';
    }
}
