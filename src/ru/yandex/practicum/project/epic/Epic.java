package ru.yandex.practicum.project.epic;
import ru.yandex.practicum.project.task.Task;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    public ArrayList<Integer> subtaskEpicId = new ArrayList<>();

    public Epic(String nameTask, String description, int id, String status) {
        super(nameTask, description, id, status);
    }

    public ArrayList<Integer> getSubtaskEpicId() {
        return subtaskEpicId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "nameEpic='" + getNameTask() + '\'' + ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + ", id=" + getId() + "\n\t" +
                "subtaskEpicId=" + subtaskEpicId + "\n\t" + "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskEpicId, epic.subtaskEpicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtaskEpicId);
    }
}
