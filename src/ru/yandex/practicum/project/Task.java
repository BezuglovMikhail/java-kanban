package ru.yandex.practicum.project;

import java.util.Objects;

public class Task {

    public String nameTask;
    public String description;
    public String status;
    public int id;

    public Task(String nameTask, String description, int id, String status) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = "NEW";
        this.id += id;
    }

    public Task() {
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "nameTask='" + nameTask +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}' + "\n\t";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(nameTask, task.nameTask) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, description, status, id);
    }
}
