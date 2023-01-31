import ru.yandex.practicum.project.manager.InMemoryTaskManager;
import ru.yandex.practicum.project.manager.Managers;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        System.out.println(manager.historyManager.getHistory());

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять");
        Epic epic1 = new Epic("Переезд", "Переезд в другой город");
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Составить список", "Список вещей для переезда"));
        subtasks1.add(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать"));

        Epic epic2 = new Epic("Прогулка с детьми", "Собрать детей взять снегокат и пойти гулять");
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Одеть детей", "Поймать и одеть детей"));

        manager.addTask(task1);
        manager.addEpic(epic1, subtasks1);
        manager.addEpic(epic2, subtasks2);

        //manager.printAllTask();

        manager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), String.valueOf(Status.DONE),
                task1.getId()));
        manager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), String.valueOf(Status.DONE),
                        epic2.getId(), epic2.getIdSubtaskEpic())),
                (new ArrayList<>(Collections.singleton(new Subtask(manager.getSubtaskList().get(6).getNameTask(),
                        manager.getSubtaskList().get(6).getDescription(), String.valueOf(Status.IN_PROGRESS),
                        manager.getSubtaskList().get(6).getId(), manager.getSubtaskList().get(6).getIdEpic())))));

        manager.findTaskId(5);
        manager.findTaskId(1);
        System.out.println(manager.findSubtaskForEpicId(5));
        System.out.println();
        System.out.println();
        System.out.println(manager.historyManager.getHistory());
        System.out.println();
        System.out.println();
        manager.findTaskId(1);
        System.out.println(manager.historyManager.getHistory());

        Task task2 = new Task("Отдых", "Отключить все гаджеты и лечь спать");
        manager.addTask(task2);

        ArrayList<Subtask> subtasks3 = new ArrayList<>();
        subtasks3.add(new Subtask(manager.getSubtaskList().get(3).getNameTask(),
                manager.getSubtaskList().get(3).getDescription(),
                String.valueOf(Status.DONE), manager.getSubtaskList().get(3).getId(),
                manager.getSubtaskList().get(3).getIdEpic()));
        subtasks3.add(new Subtask(manager.getSubtaskList().get(4).getNameTask(),
                manager.getSubtaskList().get(4).getDescription(), String.valueOf(Status.DONE),
                manager.getSubtaskList().get(4).getId(), manager.getSubtaskList().get(4).getIdEpic()));

        manager.updateEpic((new Epic(epic1.getNameTask(), epic1.getDescription(), epic1.getStatus(), epic1.getId(),
                epic1.getIdSubtaskEpic())), subtasks3);

        //manager.findTaskIdAndRemove(3);
        System.out.println(manager.findSubtaskForEpicId(2));
        System.out.println();
        System.out.println();
        System.out.println(manager.historyManager.getHistory());
        System.out.println();
        System.out.println();
        //manager.printAllTask();
        manager.findTaskIdAndRemove(1);
        manager.findTaskIdAndRemove(4);
        //manager.findTaskIdAndRemove(6);
        manager.updateTask(new Task(task2.getNameTask(), task2.getDescription(), String.valueOf(Status.IN_PROGRESS),
                task2.getId()));
        System.out.println();
        System.out.println();
        System.out.println(manager.historyManager.getHistory());
        System.out.println();
        System.out.println();
        manager.printAllTask();
        manager.cleanTask();
        //manager.printAllTask();
        System.out.println();
        System.out.println();
        System.out.println(manager.historyManager.getHistory());
    }

    @Override
    public String toString() {
        return "Main{}";
    }
}

