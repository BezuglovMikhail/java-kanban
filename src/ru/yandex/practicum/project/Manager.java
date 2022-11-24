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
                    System.out.println("Выберите тип задачи: " + "\n" +
                            "1 - Простая задача, " + "2 - Большая задача");
                    int commandTwo = scanner.nextInt();
                    if (commandTwo == 1) {

                        Task task = new Task();
                        idTask++;

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

                        task.setStatus(status[0]);
                        task.id = idTask;
                        taskList.put(idTask, task);

                    } else if (commandTwo == 2) {

                        idEpic++;
                        System.out.println("Введите название эпика");
                        String nameEpic = scanner.nextLine();
                        while (nameEpic.isEmpty()) {
                            nameEpic = scanner.nextLine();
                        }

                        System.out.println("Опишите эпик");
                        String description = scanner.nextLine();
                        String statusEpic = status[0];
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


                    } else {
                        System.out.println("Такой команды пока нет");
                    }
                    break;

                case 2:
                    System.out.println("Выберите тип задачи: " + "\n" +
                            "1 - Простая задача, " + "2 - Большая задача");
                    int commandTree = scanner.nextInt();
                    if (commandTree == 1) {
                        System.out.println("Список простых задач:");

                        for (int id : taskList.keySet()) {
                            Task task = taskList.get(id);
                            System.out.println(id + " " + task.nameTask);
                        }
                        System.out.println("Выберите задачу для изменения статуса");
                        int numberTask = scanner.nextInt();
                        updateTask(taskList.get(numberTask));
                    }
                    if (commandTree == 2) {
                        int numberDoneSubtask = 0;
                        System.out.println("Список больших задач:");
                        for (int id : taskList.keySet()) {
                            Epic epic = epicList.get(id);
                            System.out.println(id + " " + epic.nameTask);
                        }
                        System.out.println("Выберите эпик для изменения статуса подзадачи");
                        int numberEpic = scanner.nextInt();
                        for (int id : epicList.get(numberEpic).subtask.keySet()) {
                            Subtask subtask = epicList.get(numberEpic).subtask.get(id);
                            System.out.println(id + " " + subtask.nameTask + " - " + subtask.status);
                        }
                        System.out.println("Выберите подзадачу эпика для изменения статуса");
                        int numberSubtask = scanner.nextInt();
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
                    System.out.println(toString());
                    break;
                case 4:
                    if (taskList.size() == 0 && epicList.size() == 0) {
                        System.out.println("Задач нет");
                        break;
                    }

                    if (taskList.size() == 0) {
                        System.out.println("Простых задач нет");
                    }
                    if (epicList.size() == 0) {
                        System.out.println("Больших задач нет");
                    }

                    System.out.println("Выберите тип задачи: " + "\n" +
                            "1 - Простая задача, " + "2 - Большая задача");
                    int commandFour = scanner.nextInt();
                    if (commandFour == 1) {
                        System.out.println("Введите id задачи");
                        int numberTask = scanner.nextInt();
                        if (taskList.get(numberTask) == null) {
                            System.out.println("Такой задачи нет или задача удалена");
                        } else {
                            System.out.println(taskList.get(numberTask));
                        }
                    }
                    if (commandFour == 2) {
                        System.out.println("Введите id задачи");
                        int numberEpic = scanner.nextInt();
                        if (taskList.get(numberEpic) == null) {
                            System.out.println("Такой задачи нет или задача удалена");
                        } else {
                            System.out.println(epicList.get(numberEpic));
                        }
                    }
                    break;

                case 5:
                    if (epicList.size() == 0) {
                        System.out.println("Больших задач нет");
                        break;
                    }
                    System.out.println("Введите id задачи");
                    int numberEpic = scanner.nextInt();
                    if (epicList.get(numberEpic) == null) {
                        System.out.println("Такой задачи нет");
                    } else {
                        System.out.println(epicList.get(numberEpic).getNameTask());
                        System.out.println(epicList.get(numberEpic).getSubtask());
                        break;
                    }

                case 6:
                    System.out.println("Выберите тип задачи: " + "\n" +
                            "1 - Простая задача, " + "2 - Большая задача");
                    int commandSix = scanner.nextInt();

                    if (commandSix == 1) {
                        if (taskList.size() == 0) {
                            System.out.println("Задач нет");
                            break;
                        }
                        System.out.println("Введите id задачи");
                        int numberTask = scanner.nextInt();
                        taskList.remove(numberTask);
                    }
                    if (commandSix == 2) {
                        if (epicList.size() == 0) {
                            System.out.println("Задач нет");
                            break;
                        }
                        System.out.println("Введите id задачи");
                        int numberEpicRemove = scanner.nextInt();
                        epicList.remove(numberEpicRemove);
                    }

                    break;
                case 7:
                    if (taskList.size() == 0 || epicList.size() == 0) {
                        System.out.println("Задач нет");
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

    @Override
    public String toString() {
        return "Manager{" + "\n\t" +
                "taskList=" + "\n\t" + taskList.entrySet() + "\n\t" +
                "epicList=" + "\n\t" + epicList + '}' + "\n\t";
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Arrays.equals(status, manager.status);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(taskList, epicList, idEpic, idTask, idSubtask, scanner);
        result = 31 * result + Arrays.hashCode(status);
        return result;
    }
}
