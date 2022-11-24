package ru.yandex.practicum.project;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(String nameTask, String description, int id, String status) {
        super(nameTask, description, id, status);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }

    public void setSubtask(HashMap<Integer, Subtask> subtask) {
        this.subtask = subtask;
    }

    @Override
    public String toString() {
        return "Epic {" +
                "nameEpic='" + nameTask + '\'' + ", description='" + description + '\'' + ", status='" +
                status + '\'' + ", id=" + id + "\n\t" +
                "subtask=" + subtask + "\n\t";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtask, epic.subtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtask);
    }
}
