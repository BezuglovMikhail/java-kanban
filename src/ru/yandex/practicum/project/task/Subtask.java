package ru.yandex.practicum.project.task;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
    }

    public Subtask(String nameTask, String description, String status, int id, int idEpic) {
        super(nameTask, description, status, id);
        this.idEpic = idEpic;
    }

    public int getNameEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "idEpic='" + idEpic + '\'' +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", id=" + getId() +
                "} "+ "\n\t";
    }

}
