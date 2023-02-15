package ru.yandex.practicum.project.task;

import ru.yandex.practicum.project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static ru.yandex.practicum.project.task.NameTask.EPIC;



public class Epic extends Task {
    private ArrayList<Integer> idSubtaskEpic = new ArrayList<>();

    public Epic(String nameTask, String description) {
        super(nameTask, description);
    }

   /* public Epic(String nameTask, String description, Status status, int id) {
        super(nameTask, description, status, id);
        setType(EPIC);
    }

   /* public Epic(String nameTask, String description, Status status, int id, ArrayList<Integer> idSubtaskEpic) {
        super(nameTask, description, status, id);
        this.idSubtaskEpic = idSubtaskEpic;
        setType(EPIC);
    }

    public Epic(String nameTask, String description, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, startTime, duration);
        setType(EPIC);
    }*/


   public Epic(String nameTask, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, status, id);
        setStartTime(startTime);
        setDuration(duration);
        setType(EPIC);
    }


    public Epic(String nameTask, String description, Status status, int id, ArrayList<Integer> idSubtaskEpic, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, status, id);
        this.idSubtaskEpic = idSubtaskEpic;
        setStartTime(startTime);
        setDuration(duration);
        setType(EPIC);



       // long durationEpic = 0;
       // for (int idSubtask : idSubtaskEpic) {
        //    duration =
       // }
       //setDuration();
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
                ", idSubtaskEpic=" + idSubtaskEpic +
                ", type'" + getType() + '\'' +
                ", startTime'" + getStartTime() + '\'' +
                ", duration'" + getDuration() + "} " + "\n\t";
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
