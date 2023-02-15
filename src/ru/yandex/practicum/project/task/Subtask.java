package ru.yandex.practicum.project.task;

import ru.yandex.practicum.project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static ru.yandex.practicum.project.task.NameTask.SUBTASK;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
        setType(SUBTASK);
    }

    public Subtask(String nameTask, String description, Status status, int id, int idEpic) {
        super(nameTask, description, status, id);
        this.idEpic = idEpic;
       setType(SUBTASK);
    }

    public Subtask(String nameTask, String description, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, startTime, duration);
        setType(SUBTASK);
    }

    public Subtask(String nameTask, String description, Status status, int id, LocalDateTime startTime, Duration duration, int idEpic) {
        super(nameTask, description, status, id, startTime, duration);
        this.idEpic = idEpic;
        setType(SUBTASK);
    }



    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "\n\t" + "Subtask{" +
                "idEpic='" + idEpic + '\'' +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", id='" + getId() + '\'' +
                ", type'" + getType() + '\'' +
                ", startTime'" + getStartTime() + '\'' +
                ", duration'" + getDuration() +
                "}" + "\n\t";
    }
}
