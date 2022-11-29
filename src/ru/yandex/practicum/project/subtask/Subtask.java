package ru.yandex.practicum.project.subtask;
import ru.yandex.practicum.project.task.Task;

public class Subtask extends Task {
    public String nameEpic;

    public Subtask(String nameEpic, String nameTask, String description, int id, String status) {
        super(nameTask, description, id, status);
        this.nameEpic = nameEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "nameEpic='" + nameEpic + '\'' +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", id=" + getId() +
                "} "+ "\n\t";
    }

}
