package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.task.*;
import ru.yandex.practicum.project.node.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager<Task> {

    private final LinkedList<Task> customLinkedList = new LinkedList<>();
    private Node<Task> head;
    private Node<Task> tail;

    private final LinkedHashSet<Task> historyTaskList = new LinkedHashSet<>();

    public HashMap<Integer, Node<Task>> historyTaskMap = new HashMap<>();

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(null, task, oldTail);
            tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        customLinkedList.addLast(task);
        historyTaskMap.put(task.getId(), newNode);
    }

    public void getTasks (LinkedList<Task> customLinkedList) {
        historyTaskList.addAll(customLinkedList);
    }

    public void removeNode(Node<Task> node) {
        historyTaskList.remove(node.task);
    }

    @Override
    public void add(Task task) {
       linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyTaskMap.get(id));
    }

    @Override
    public LinkedHashSet<Task> getHistory() {
        getTasks(customLinkedList);
        return historyTaskList;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                ", historyTaskList=" + historyTaskList +
                '}';
    }
}
