package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.subtask.Subtask;
import ru.yandex.practicum.project.task.Task;
import ru.yandex.practicum.project.epic.Epic;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import static ru.yandex.practicum.project.status.Status.DONE;
import static ru.yandex.practicum.project.status.Status.IN_PROGRESS;

public class Manager {
    public HashMap<Integer, Task> taskList = new HashMap<>();
    public HashMap<Integer, Epic> epicList = new HashMap<>();
    public HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    public int id = 0;
    Scanner scanner = new Scanner(System.in);

    public void addTask() {
        Task task = new Task();
        String nameTask = "";
        String description = "";

        System.out.println("Введите название задачи");
        while (nameTask.isEmpty()) {
            nameTask = scanner.nextLine();
            task.setNameTask(nameTask);
        }

        while (description.isEmpty()) {
            System.out.println("Опишите задачу");
            description = scanner.nextLine();
            System.out.println();
            task.setDescription(description);
        }

        id++;
        task.setStatus(String.valueOf(Status.NEW));
        task.setId(id);
        taskList.put(id, task);
    }

    public void addEpic() {
        System.out.println("Введите название эпика");
        String nameEpic = scanner.nextLine();

        while (nameEpic.isEmpty()) {
            nameEpic = scanner.nextLine();
        }

        System.out.println("Опишите эпик");
        String description = scanner.nextLine();
        String statusEpic = String.valueOf(Status.NEW);
        id++;
        Epic epic = new Epic(nameEpic, description, id, statusEpic);

        while (true) {
            System.out.println("Если подзадачи закончились введите: 0");
            System.out.println("Введите название подзадачи эпика: " + nameEpic);
            String nameSubtask = scanner.nextLine();
            if (Objects.equals(nameSubtask, "0")) {
                break;
            }
            id++;
            System.out.println("Опишите подзадачу эпика");
            String descriptionSubtask = scanner.nextLine();
            String statusSubtask = String.valueOf(Status.NEW);
            Subtask subtask = new Subtask(nameEpic, nameSubtask, descriptionSubtask, id, statusSubtask);
            epic.subtaskEpicId.add(id);
            subtaskList.put(id, subtask);
        }
        epicList.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (Objects.equals(task.getStatus(), "DONE")) {
            System.out.println("Задача выполнена!");
        } else {
            System.out.println("Выбирите новый статус: " + "\n" +
                    IN_PROGRESS + "\n" +
                    DONE);
            String nameStatus = scanner.next();
            if (Objects.equals(nameStatus, String.valueOf(IN_PROGRESS)) || Objects.equals(nameStatus, String.valueOf(DONE))) {
                task.setStatus(nameStatus);
            } else {
                System.out.println("Введён неверный статус");
            }
        }
    }

    public int selectTask() {
        System.out.println("Выберите тип задачи: " + "\n" +
                "1 - Простая задача, " + "2 - Большая задача");
        return scanner.nextInt();
    }

    public void printListTask() {
        System.out.println("Список простых задач:");
        for (int id : taskList.keySet()) {
            Task task = taskList.get(id);
            System.out.println(id + " " + task.getNameTask());
        }
    }

    public void printListEpic() {
        System.out.println("Список больших задач:");
        for (int id : epicList.keySet()) {
            Epic epic = epicList.get(id);
            System.out.println(id + " " + epic.getNameTask());
        }
    }

    public int enterId() {
        System.out.println("Введите id задачи");
        return scanner.nextInt();
    }

    public void findTaskIdAndPrint(HashMap<Integer, Epic> epicList, HashMap<Integer, Task> taskList, int taskId) {
        if (!(epicList.get(taskId) == null)) {
            System.out.println(epicList.get(taskId));
        } else if (!(subtaskList.get(taskId) == null)) {
            System.out.println(subtaskList.get(taskId));
        } else if (!(taskList.get(taskId) == null)) {
            System.out.println(taskList.get(taskId));
        } else {
            System.out.println("Такой задачи нет или задача удалена");
        }
    }

    public void findTaskIdAndRemove(HashMap<Integer, Epic> epicList, HashMap<Integer, Task> taskList, int taskId) {
        if (!(epicList.get(taskId) == null)) {
            epicList.remove(taskId);
        } else if (!(subtaskList.get(taskId) == null)) {

            int idRemoveSubtask = 0;
            int idEpic = 0;
            for (int i : epicList.keySet()) {
                Epic epic = epicList.get(i);

                for (int id : epic.getSubtaskEpicId())
                    if (id == taskId) {
                        idRemoveSubtask = epic.subtaskEpicId.indexOf(taskId);
                        idEpic = i;
                    }
            }

            epicList.get(idEpic).subtaskEpicId.remove(idRemoveSubtask);
            subtaskList.remove(taskId);
        } else if (!(taskList.get(taskId) == null)) {
            taskList.remove(taskId);
        } else {
            System.out.println("Неверный тип задачи или задача удалена");
        }
    }

    public boolean checkTaskAndEpic() {
        if (taskList.size() == 0 && epicList.size() == 0) {
            System.out.println("Задач нет");
            return false;
        }
        return true;
    }

    public boolean checkTaskSize() {
        if (taskList.size() == 0) {
            System.out.println("Задач нет");
            return false;
        }
        return true;
    }

    public boolean checkEpicSize() {
        if (epicList.size() == 0) {
            System.out.println("Задач нет");
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return id == manager.id && Objects.equals(taskList, manager.taskList) && Objects.equals(epicList, manager.epicList) && Objects.equals(scanner, manager.scanner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskList, epicList, id, scanner);
    }

    public boolean checkEpicNull(int numberEpic) {
        if (epicList.get(numberEpic) == null) {
            System.out.println("Неверный тип задачи или задача удалена");
            return false;
        }
        return true;
    }

    public boolean checkTaskNull(int numberTask) {
        if (taskList.get(numberTask) == null) {
            System.out.println("Такой задачи нет или задача удалена");
            return false;
        }
        return true;
    }

    public boolean checkSubtaskNull(int numberSubtask) {
        if (subtaskList.get(numberSubtask) == null) {
            System.out.println("Такой подзадачи в этом Эпике нет");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Manager{" + "\n\t" +
                "taskList=" + "\n\t" + taskList.entrySet() + "\n\t" +
                "epicList=" + "\n\t" + epicList + '}' + "\n\t";
    }

}
