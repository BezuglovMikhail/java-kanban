package ru.yandex.practicum.project;

public class Subtask extends Task {

    public Subtask(String nameTask, String description, int id, String status) {
        super(nameTask, description, id, status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                "} " + "\n\t";
    }
}
