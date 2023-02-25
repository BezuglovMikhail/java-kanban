package ru.yandex.practicum.project.task;

import jdk.jshell.Snippet;
import ru.yandex.practicum.project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.yandex.practicum.project.task.NameTask.TASK;

public class Task {
    private final String nameTask;
    private final String description;
    private Status status;
    private int id;
    private NameTask type;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
    }

    public Task(String nameTask, String description, LocalDateTime startTime, Duration duration) {
        this.nameTask = nameTask;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public Task(String nameTask, String description, LocalDateTime startTime, Duration duration, NameTask type) {
        this.nameTask = nameTask;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
        this.type = type;
    }

    public Task(String nameTask, String description, Status status, int id) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String nameTask, String description, Status status, int id, LocalDateTime startTime) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
    }

    public Task(String nameTask, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public Task(String nameTask, String description, Status status, int id, LocalDateTime startTime, Duration duration, LocalDateTime endTime, NameTask type) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
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

    public NameTask getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(NameTask type) {
        this.type = type;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime startTime, Duration duration) {
        this.endTime = startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "\n\t" + "Task{" +
                "nameTask='" + nameTask +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id='" + id + '\'' +
                ", type'" + type + '\'' +
                ", startTime'" + startTime + '\'' +
                ", duration'" + duration +
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
