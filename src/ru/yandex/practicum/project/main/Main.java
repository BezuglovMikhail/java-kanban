package ru.yandex.practicum.project.main;

import ru.yandex.practicum.project.manager.Manager;
import ru.yandex.practicum.project.status.Status;
import static ru.yandex.practicum.project.status.Status.IN_PROGRESS;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);
        boolean startMenu = true;
        while (startMenu) {

            printMenu();
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    int commandTwo = manager.selectTask();
                    if (commandTwo == 1) {
                        manager.addTask();
                    } else if (commandTwo == 2) {
                        manager.addEpic();
                    } else {
                        System.out.println("Такой команды пока нет");
                    }
                    break;

                case 2:
                    if (!manager.checkTaskAndEpic()) {
                        break;
                    }
                    int commandTree = manager.selectTask();
                    if (commandTree == 1) {
                        if (!manager.checkTaskSize()) {
                            break;
                        }
                        manager.printListTask();
                        System.out.println("Выберите задачу для изменения статуса");
                        int numberTask = scanner.nextInt();
                        if (!manager.checkTaskNull(numberTask)) {
                            break;
                        }
                        manager.updateTask(manager.taskList.get(numberTask));
                    }

                    if (commandTree == 2) {
                        if (!manager.checkEpicSize()) {
                            break;
                        }
                        int numberDoneSubtask = 0;
                        manager.printListEpic();
                        System.out.println("Выберите эпик для изменения статуса подзадачи");
                        int numberEpic = scanner.nextInt();
                        if (!manager.checkEpicNull(numberEpic)) {
                            break;
                        }

                        for (int id : manager.epicList.get(numberEpic).getSubtaskEpicId()) {
                            System.out.println(manager.subtaskList.get(id).getId() + " "
                                    + manager.subtaskList.get(id).getNameTask() + " - "
                                    + manager.subtaskList.get(id).getStatus());
                        }

                        System.out.println("Выберите подзадачу эпика для изменения статуса");
                        int numberSubtask = scanner.nextInt();
                        if (!manager.checkSubtaskNull(numberSubtask)) {
                            break;
                        }
                        manager.updateTask(manager.subtaskList.get(numberSubtask));
                        manager.epicList.get(numberEpic).setStatus(String.valueOf(IN_PROGRESS));

                        for (int id : manager.epicList.get(numberEpic).getSubtaskEpicId())
                            if (manager.subtaskList.get(id).getStatus().equals("DONE")) {
                                numberDoneSubtask += 1;
                        }
                        if (numberDoneSubtask == manager.epicList.get(numberEpic).getSubtaskEpicId().size()) {
                            manager.epicList.get(numberEpic).setStatus(String.valueOf(Status.DONE));
                        }
                    }
                    break;

                case 3:
                    if (!manager.checkTaskAndEpic()) {
                        break;
                    }

                    System.out.println(manager.taskList + "\n" +
                            manager.epicList + "\n" +
                            manager.subtaskList + "\n");
                    break;

                case 4:
                    if (!manager.checkTaskAndEpic()) {
                        break;
                    }
                    int taskId = manager.enterId();
                    manager.findTaskIdAndPrint(manager.epicList, manager.taskList, taskId);
                    break;

                case 5:
                    if (!manager.checkEpicSize()) {
                        break;
                    }
                    int numberEpic = manager.enterId();
                    if (!manager.checkEpicNull(numberEpic)) {
                        break;
                    } else {
                        System.out.println(manager.epicList.get(numberEpic).getNameTask());
                        if (manager.epicList.get(numberEpic).getSubtaskEpicId().size() == 0) {
                            System.out.println("Список подчадач пуст");
                            System.out.println();
                        }
                        for (int id : manager.epicList.get(numberEpic).getSubtaskEpicId()) {
                            System.out.println(manager.subtaskList.get(id));
                        }
                        break;
                    }

                case 6:
                    if (!manager.checkTaskAndEpic()) {
                        break;
                    }
                    int numberTask = manager.enterId();
                    manager.findTaskIdAndRemove(manager.epicList, manager.taskList, numberTask);
                    break;

                case 7:
                    if (!manager.checkTaskAndEpic()) {
                        break;
                    }
                    manager.taskList.clear();
                    manager.epicList.clear();
                    manager.id = 0;
                    break;

                case 0:
                    startMenu = false;
                    break;

                default:
                    System.out.println("Такой команды пока нет");
            }
        }
    }

    public static void printMenu() {
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
        return "Main{}";
    }
}
