package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.*;
import ru.yandex.practicum.project.node.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager<Task> {

    private Node<Task> head;
    private Node<Task> tail;

    public HashMap<Integer, Node<Task>> historyTaskMap = new HashMap<>();

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(null, task, oldTail);
            tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historyTaskMap.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {
       linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyTaskMap.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyTaskList = new ArrayList<>();
        for(Node node : historyTaskMap.values()) {
            historyTaskList.add((Task) node.getTask());
        }
        return historyTaskList;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                ", historyTaskMap=" + historyTaskMap +
                '}';
    }
}
