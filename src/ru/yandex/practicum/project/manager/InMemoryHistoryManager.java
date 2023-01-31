package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.node.Node;
import ru.yandex.practicum.project.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class InMemoryHistoryManager implements HistoryManager<Task> {

    private Node<Task> head;
    private Node<Task> tail;

    private final HashMap<Integer, Node<Task>> historyTaskMap = new HashMap<>();

    public HashMap<Integer, Node<Task>> getHistoryTaskMap() {
        return historyTaskMap;
    }
    //private ArrayList<Task> historyTaskList = new ArrayList<>();

    public void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(null, task, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
            head.setPrev(newNode);
        }
        //newNode.setPrev(oldTail);
        //newNode.setNext(head);

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
    public void clear() {
        this.historyTaskMap.clear();
        this.head = null;
        this.tail = null;
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
        if (head != null) {
            /*for (Node<Task> node : getHistoryTaskMap().values()) {

            //for (Node<Task> node = head; node == tail; node.getNext()) {
                historyTaskList.add(node.getTask());
            }*/
            Node<Task> node = head;
            while (node != tail) {
                historyTaskList.add(node.getTask());
                node = node.getNext();
            }
            historyTaskList.add(tail.getTask());
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
