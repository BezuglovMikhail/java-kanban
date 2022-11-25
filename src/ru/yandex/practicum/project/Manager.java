package ru.yandex.practicum.project;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.Arrays;

public class Manager {
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, Epic> epicList = new HashMap<>();
    String[] status = {"NEW", "IN_PROGRESS", "DONE"};
    int idEpic = 0;
    int idTask = 0;
    int idSubtask = 0;
    Scanner scanner = new Scanner(System.in);

    public void menu() {
        boolean startMenu = true;
        while (startMenu) {

            printMenu();
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    int commandTwo = selectTask();
                    if (commandTwo == 1) {
                        addTask();

                    } else if (commandTwo == 2) {
                        addEpic();
                    } else {
                        System.out.println("Такой команды пока нет");
                    }
                    break;

                case 2:
                    if (!checkTaskAndEpic()) {
                        break;
                    }
                    int commandTree = selectTask();

                    if (commandTree == 1) {
                        if (!checkTaskSize()) {
                            break;
                        }
                        printListTask();
                        System.out.println("Выберите задачу для изменения статуса");
                        int numberTask = scanner.nextInt();
                        if (!checkTaskNull(numberTask)) {
                            break;
                        }
                        checkTaskId(taskList, numberTask);
                        updateTask(taskList.get(numberTask));
                    }

                    if (commandTree == 2) {
                        if (!checkEpicSize()) {
                            break;
                        }
                        int numberDoneSubtask = 0;
                        printListEpic();
                        System.out.println("Выберите эпик для изменения статуса подзадачи");
                        int numberEpic = scanner.nextInt();
                        if (!checkEpicNull(numberEpic)) {
                            break;
                        }

                        for (int id : epicList.get(numberEpic).subtask.keySet()) {
                            Subtask subtask = epicList.get(numberEpic).subtask.get(id);
                            System.out.println(id + " " + subtask.nameTask + " - " + subtask.status);
                        }

                        System.out.println("Выберите подзадачу эпика для изменения статуса");
                        int numberSubtask = scanner.nextInt();
                        if (!checkSubtaskNull(numberEpic, numberSubtask)) {
                            break;
                        }
                        updateTask(epicList.get(numberEpic).subtask.get(numberSubtask));
                        epicList.get(numberEpic).status = status[1];

                        for (int i = 1; i <= epicList.get(numberEpic).subtask.size(); i++) {
                            Subtask subtask = epicList.get(numberEpic).subtask.get(i);
                            if (subtask.status.equals("DONE")) {
                                numberDoneSubtask += 1;
                            }
                        }
                        if (numberDoneSubtask == epicList.get(numberEpic).subtask.size()) {
                            epicList.get(numberEpic).status = status[2];
                        }
                    }
                    break;

                case 3:
                    if (!checkTaskAndEpic()) {
                        break;
                    }
                    System.out.println(toString());
                    break;

                case 4:
                    if (!checkTaskAndEpic()) {
                        break;
                    }
                    int commandFour = selectTask();
                    if (commandFour == 1) {
                        if (!checkTaskSize()) {
                            break;
                        }
                        int numberTask = enterId();
                        if (!checkTaskNull(numberTask)) {
                            break;
                        }
                        checkTaskId(taskList, numberTask);
                    }
                    if (commandFour == 2) {
                        if (!checkEpicSize()) {
                            break;
                        }
                        int numberEpic = enterId();
                        if (!checkEpicNull(numberEpic)) {
                            break;
                        }
                        checkEpicId(epicList, numberEpic);
                    }
                    break;

                case 5:
                    if (!checkEpicSize()) {
                        break;
                    }
                    int numberEpic = enterId();
                    if (!checkEpicNull(numberEpic)) {
                        break;
                    } else {
                        System.out.println(epicList.get(numberEpic).getNameTask());
                        System.out.println(epicList.get(numberEpic).getSubtask());
                        break;
                    }

                case 6:
                    if (!checkTaskAndEpic()) {
                        break;
                    }
                    int commandSix = selectTask();
                    if (commandSix == 1) {
                        if (!checkTaskSize()) {
                            break;
                        }
                        int numberTask = enterId();
                        if (!checkTaskNull(numberTask)) {
                            break;
                        }
                        taskList.remove(numberTask);
                    }
                    if (commandSix == 2) {
                        if (!checkEpicSize()) {
                            break;
                        }
                        int numberEpicRemove = enterId();
                        if (!checkEpicNull(numberEpicRemove)) {
                            break;
                        }
                        epicList.remove(numberEpicRemove);
                    }
                    break;

                case 7:
                    if (!checkTaskAndEpic()) {
                        break;
                    }
                    taskList.clear();
                    epicList.clear();
                    idEpic = 0;
                    idTask = 0;
                    break;

                case 0:
                    startMenu = false;
                    break;

                default:
                    System.out.println("Такой команды пока нет");
            }
        }
    }

    public void printMenu() {
        System.out.println("Что вы хотите сделать: " + "\n" +
                "1 - Добавить задачу," + "\n" +
                "2 - Изменить статус задачи," + "\n" +
                "3 - Показать список всех задач," + "\n" +
                "4 - Показать задачу по её id," + "\n" +
                "5 - Показать список подзадач для эпика с id," + "\n" +
                "6 - Удалить задачу по её id," + "\n" +
                "7 - Удалить все задачи," + "\n" +
                "0 - Завершить программу.");
    }

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
        idTask++;
        task.setStatus(status[0]);
        task.id = idTask;
        taskList.put(idTask, task);
    }

    public void addEpic() {
        System.out.println("Введите название эпика");
        String nameEpic = scanner.nextLine();

        while (nameEpic.isEmpty()) {
            nameEpic = scanner.nextLine();
        }

        System.out.println("Опишите эпик");
        String description = scanner.nextLine();
        String statusEpic = status[0];
        idEpic++;
        Epic epic = new Epic(nameEpic, description, idEpic, statusEpic);
        idSubtask = 0;

        while (true) {
            idSubtask++;
            System.out.println("Если подзадачи закончились введите: 0");
            System.out.println("Введите название подзадачи эпика: " + nameEpic);
            String nameSubtask = scanner.nextLine();
            if (Objects.equals(nameSubtask, "0")) {
                break;
            }
            System.out.println("Опишите подзадачу эпика");
            String descriptionSubtask = scanner.nextLine();
            String statusSubtask = status[0];
            Subtask subtask = new Subtask(nameSubtask, descriptionSubtask, idSubtask, statusSubtask);
            epic.subtask.put(idSubtask, subtask);
        }
        epicList.put(idEpic, epic);
    }

    public void updateTask(Task task) {
        if (Objects.equals(task.getStatus(), "DONE")) {
            System.out.println("Задача выполнена!");
        } else {
            System.out.println("Выбирите новый статус: " + "\n" +
                    "1 - " + status[1] + "\n" +
                    "2 - " + status[2]);
            int numberStatus = scanner.nextInt();
            task.setStatus(status[numberStatus]);
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
            System.out.println(id + " " + task.nameTask);
        }
    }

    public void printListEpic() {
        System.out.println("Список больших задач:");
        for (int id : epicList.keySet()) {
            Epic epic = epicList.get(id);
            System.out.println(id + " " + epic.nameTask);
        }
    }

    public int enterId() {
        System.out.println("Введите id задачи");
        return scanner.nextInt();
    }

    public void checkTaskId(HashMap<Integer, Task> taskList, int numberTask) {
        if (taskList.get(numberTask) == null) {
            System.out.println("Такой задачи нет или задача удалена");
        } else {
            System.out.println(taskList.get(numberTask));
        }
    }

    public void checkEpicId(HashMap<Integer, Epic> epicList, int numberTask) {
        if (epicList.get(numberTask) == null) {
            System.out.println("Такой задачи нет или задача удалена");
        } else {
            System.out.println(epicList.get(numberTask));
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Arrays.equals(status, manager.status);
    }

    public boolean checkEpicNull(int numberEpic) {
        if (epicList.get(numberEpic) == null) {
            System.out.println("Такой задачи нет или задача удалена");
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

    public boolean checkSubtaskNull(int numberEpic, int numberSubtask) {
        if (epicList.get(numberEpic).subtask.get(numberSubtask) == null) {
            System.out.println("Такой задачи нет");
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

    @Override
    public int hashCode() {
        int result = Objects.hash(taskList, epicList, idEpic, idTask, idSubtask, scanner);
        result = 31 * result + Arrays.hashCode(status);
        return result;
    }
}
