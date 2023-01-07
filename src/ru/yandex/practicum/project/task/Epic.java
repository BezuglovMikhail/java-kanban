package ru.yandex.practicum.project.task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> idSubtaskEpic = new ArrayList<>();

    public Epic(String nameTask, String description) {
        super(nameTask, description);
    }

    public Epic(String nameTask, String description, String status, int id, ArrayList<Integer> idSubtaskEpic) {
        super(nameTask, description, status, id);
        this.idSubtaskEpic = idSubtaskEpic;
    }

    public ArrayList<Integer> getIdSubtaskEpic() {
        return idSubtaskEpic;
    }

    public void setIdSubtaskEpic(ArrayList<Integer> idSubtaskEpic) {
        this.idSubtaskEpic = idSubtaskEpic;
    }

    @Override
    public String toString() {
        return "\n\t" + "Epic{" +
                "nameEpic='" + getNameTask() + '\'' + ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + ", id='" + getId() + '\'' +
                ", idSubtaskEpic=" + idSubtaskEpic + "} " + "\n\t";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idSubtaskEpic, epic.idSubtaskEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSubtaskEpic);
    }
}
