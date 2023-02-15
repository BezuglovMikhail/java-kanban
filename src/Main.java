import ru.yandex.practicum.project.manager.InMemoryTaskManager;
import ru.yandex.practicum.project.manager.Managers;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ArrayList;

import static java.time.Month.FEBRUARY;
import static ru.yandex.practicum.project.status.Status.*;

public class Main {

    public static void main(String[] args) throws IOException {

        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        System.out.println(manager.historyManager.getHistory());

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15));

        Task task2 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 35), Duration.ofMinutes(15));

        Epic epic1 = new Epic("Переезд", "Переезд в другой город");
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Составить список", "Список вещей для переезда",
                LocalDateTime.of(2023, FEBRUARY, 13, 20, 30), Duration.ofMinutes(15)));
        subtasks1.add(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать",
                LocalDateTime.of(2023, FEBRUARY, 13, 0, 0), Duration.ofMinutes(15)));

        Epic epic2 = new Epic("Прогулка с детьми", "Собрать детей взять снегокат и пойти гулять");
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Одеть детей", "Поймать и одеть детей",
                LocalDateTime.of(2023, FEBRUARY, 13, 18, 40), Duration.ofMinutes(15)));

        manager.addTask(task1);
        manager.printAllTask();
        //manager.addEpic(epic1, subtasks1);
       // manager.addEpic(epic2, subtasks2);
        manager.findTaskId(5);
        manager.findTaskId(3);
        manager.findTaskId(2);
        manager.findTaskId(1);
       // manager.addTask(task2);
        System.out.println(manager.getPrioritizedTasks());
        manager.findTaskId(3);
        //manager.findTaskIdAndRemove(1);
        //manager.findTaskIdAndRemove(1);
        //manager.findTaskIdAndRemove(3);
        //manager.findTaskIdAndRemove(5);
        manager.findTaskId(2);
        manager.findTaskId(4);
        System.out.println(manager.getPrioritizedTasks());

        manager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), DONE,
         task1.getId(), task1.getStartTime(), task1.getDuration()));

        System.out.println(manager.getPrioritizedTasks());
        System.out.println(manager.getPrioritizedTasks());
        /*manager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), NEW,
                        epic2.getId(), epic2.getIdSubtaskEpic(),
                        manager.getEpicList().get(5).getStartTime(),
                        manager.getEpicList().get(5).getDuration())),
                new ArrayList<>(Collections.singleton
                        (new Subtask(manager.getSubtaskList().get(6).getNameTask(),
                                manager.getSubtaskList().get(6).getDescription(), IN_PROGRESS,
                                manager.getSubtaskList().get(6).getId(),
                                manager.getSubtaskList().get(6).getStartTime(),
                                manager.getSubtaskList().get(6).getDuration(),
                                manager.getSubtaskList().get(6).getIdEpic()))));*/
    }

    @Override
    public String toString() {
        return "Main{}";
    }
}

