package ru.yandex.practicum.project.node;

import ru.yandex.practicum.project.task.Task;

public class Node<Task> {

    private Task task;
    private Node<Task> next;
    private Node<Task> prev;

   //private Node<Task> head;
    //private Node<Task> tail;

    public Node(Task task) {
        this.task = task;
    }

    public Node(Node<Task> prev, Task task, Node<Task> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node<Task> getNext() {
        return next;
    }

   /* public Node<Task> getHead() {
        return head;
    }

    public void setHead(Node<Task> head) {
        this.head = head;
    }

    public Node<Task> getTail() {
        return tail;
    }

    public void setTail(Node<Task> tail) {
        this.tail = tail;
    }*/

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public Node<Task> getPrev() {
        return prev;
    }

    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }
}

