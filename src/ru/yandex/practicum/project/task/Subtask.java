package ru.yandex.practicum.project.task;

public class Subtask extends Task {
    private String nameEpic;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
    }

    public Subtask(String nameTask, String description, String status, int id, String nameEpic) {
        super(nameTask, description, status, id);
        this.nameEpic = nameEpic;
    }

    public String getNameEpic() {
        return nameEpic;
    }

    public void setNameEpic(String nameEpic) {
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
