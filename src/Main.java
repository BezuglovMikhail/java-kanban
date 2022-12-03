import ru.yandex.practicum.project.manager.Manager;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import java.util.Collections;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять");
        Epic epic1 = new Epic("Переезд", "Переезд в другой город");
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Составить список", "Список вещей для переезда"));
        subtasks1.add(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать"));

        Epic epic2 = new Epic("Прогулка с детьми", "Собрать детей, взять снегокат и пойти гулять");
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Одеть детей", "Поймать и одеть детей"));

        manager.addTask(task1);
        manager.addEpic(epic1, subtasks1);
        manager.addEpic(epic2, subtasks2);

        System.out.println(manager.taskList + "\n" +
                manager.epicList + "\n" +
                manager.subtaskList + "\n");

        manager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), String.valueOf(Status.DONE),
                task1.getId()));
        manager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), String.valueOf(Status.DONE), epic2.getId(),
                        epic2.getIdSubtaskEpic())),
                (new ArrayList<>(Collections.singleton(new Subtask(manager.subtaskList.get(6).getNameTask(),
                        manager.subtaskList.get(6).getDescription(), String.valueOf(Status.IN_PROGRESS),
                        manager.subtaskList.get(6).getId(), manager.subtaskList.get(6).getNameEpic())))));

        manager.findTaskId(1);
        manager.findTaskId(5);
        System.out.println(manager.findSubtaskForEpicId(5));

        ArrayList<Subtask> subtasks3 = new ArrayList<>();
        subtasks3.add(new Subtask(manager.subtaskList.get(3).getNameTask(), manager.subtaskList.get(3).getDescription(),
                String.valueOf(Status.DONE), manager.subtaskList.get(3).getId(),
                manager.subtaskList.get(3).getNameEpic()));
        subtasks3.add(new Subtask(manager.subtaskList.get(4).getNameTask(), manager.subtaskList.get(4).getDescription(),
                String.valueOf(Status.DONE), manager.subtaskList.get(4).getId(),
                manager.subtaskList.get(4).getNameEpic()));

        manager.updateEpic((new Epic(epic1.getNameTask(), epic1.getDescription(), epic1.getStatus(), epic1.getId(),
                epic1.getIdSubtaskEpic())), subtasks3);

        manager.findTaskIdAndRemove(4);

        System.out.println(manager.findSubtaskForEpicId(2));
        manager.findTaskIdAndRemove(3);

        System.out.println(manager.taskList + "\n" +
                manager.epicList + "\n" +
                manager.subtaskList + "\n");

        manager.cleanTask();

        System.out.println(manager.taskList + "\n" +
                manager.epicList + "\n" +
                manager.subtaskList + "\n");
    }

    @Override
    public String toString() {
        return "Main{}";
    }
}
