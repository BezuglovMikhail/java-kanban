package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.node.Node;
import ru.yandex.practicum.project.task.Task;

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

    public void removeNode(Node<Task> nodeDel) {
        if (head == null || nodeDel == null) {
            return;
        }
        if (head == nodeDel) {
            head = nodeDel.getNext();
        }
        if (nodeDel.getNext() != null) {
            nodeDel.getNext().setPrev(nodeDel.getPrev());
        }
        if (nodeDel.getPrev() != null) {
            nodeDel.getPrev().setNext(nodeDel.getNext());
        }
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
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyTaskList = new ArrayList<>();
        for (Node<Task> node : historyTaskMap.values()) {
            historyTaskList.add(node.getTask());
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
