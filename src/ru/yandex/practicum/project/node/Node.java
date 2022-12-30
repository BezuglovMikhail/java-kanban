package ru.yandex.practicum.project.node;

import ru.yandex.practicum.project.task.Task;

public class Node<Task> {

        public Task task;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Node<Task> prev, Task task, Node<Task> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

