package ru.yandex.practicum.project.task;

import ru.yandex.practicum.project.status.Status;

import java.util.Objects;

import static ru.yandex.practicum.project.task.NameTask.TASK;

public class Task {
    private final String nameTask;
    private final String description;
    private Status status;
    private int id;
    private NameTask type;

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        setType(TASK);
    }

    public Task(String nameTask, String description, Status status, int id) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
        this.id = id;
        setType(TASK);
    }

    public String getNameTask() {
        return nameTask;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(NameTask type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "\n\t" + "Task{" +
                "nameTask='" + nameTask +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id='" + id + '\'' +
                '}' + "\n\t";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(nameTask, task.nameTask)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, description, status, id);
    }
}
