package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import static ru.yandex.practicum.project.status.Status.DONE;
import static ru.yandex.practicum.project.status.Status.IN_PROGRESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Manager {
    public HashMap<Integer, Task> taskList = new HashMap<>();
    public HashMap<Integer, Epic> epicList = new HashMap<>();
    public HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    public int id = 0;
    Scanner scanner = new Scanner(System.in);

    public Task addTask(Task task) {
        id++;
        task.setStatus(String.valueOf(Status.NEW));
        task.setId(id);
        taskList.put(id, task);
        return task;
    }

    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) {
        id++;
        epic.setId(id);
        epic.setStatus(String.valueOf(Status.NEW));

        ArrayList<Integer> idSubtaskEpic = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            id++;
            subtask.setId(id);
            subtask.setStatus(String.valueOf(Status.NEW));
            subtask.setNameEpic(epic.getNameTask());
            idSubtaskEpic.add(id);
            subtaskList.put(id, subtask);
        }
        epic.setIdSubtaskEpic(idSubtaskEpic);
        epicList.put(epic.getId(), epic);
        return epic;
    }

    public Task updateTask(Task task) {
        if (Objects.equals(task.getStatus(), String.valueOf(IN_PROGRESS))
                || Objects.equals(task.getStatus(), String.valueOf(DONE))) {
            task.setStatus(task.getStatus());
            taskList.put(task.getId(), task);
        }
        return task;
    }

    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) {
        int numberDoneSubtask = 0;
        int numberInProgressSubtask = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals("DONE")) {
                numberDoneSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                numberInProgressSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            }
        }

        if (numberDoneSubtask == subtasks.size()) {
            epic.setStatus(String.valueOf(Status.DONE));

            epicList.put(epic.getId(), epic);
        } else if (numberInProgressSubtask >= 1) {
            epic.setStatus(String.valueOf(IN_PROGRESS));
            epicList.put(epic.getId(), epic);
        }
        return epic;
    }

    public void findTaskId(int taskId) {
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

    public void findTaskIdAndRemove(int taskId) {
        if (!(epicList.get(taskId) == null)) {

            for (int idSubtask : epicList.get(taskId).getIdSubtaskEpic()) {
                subtaskList.remove(idSubtask);
            }
            epicList.remove(taskId);

        } else if (!(subtaskList.get(taskId) == null)) {

            for (int i : epicList.keySet()) {
                Epic epic = epicList.get(i);

                for (int id : epic.getIdSubtaskEpic()) {
                    if (id == taskId) {
                        int epicId = i;
                        int subtaskIndex = epicList.get(i).getIdSubtaskEpic().indexOf(taskId);

                        epicList.get(epicId).getIdSubtaskEpic().remove(subtaskIndex);
                        subtaskList.remove(taskId);
                        break;
                    }
                }
            }
        } else if (!(taskList.get(taskId) == null)) {
            taskList.remove(taskId);
        } else {
            System.out.println("Неверный тип задачи или задача удалена");
        }
    }

    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) {
        ArrayList<Subtask> subtasksEpicId = new ArrayList<>();
        if (!checkEpicNull(epicId)) {
        } else {
            if (epicList.get(epicId).getIdSubtaskEpic().size() == 0) {
            }
            for (int id : epicList.get(epicId).getIdSubtaskEpic()) {
                subtasksEpicId.add(subtaskList.get(id));
            }
        }
        return subtasksEpicId;
    }

    public void cleanTask() {
        epicList.clear();
        subtaskList.clear();
        taskList.clear();
    }

    public boolean checkEpicNull(int numberEpic) {
        if (epicList.get(numberEpic) == null) {
            System.out.println("Неверный тип задачи или задача удалена");
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return id == manager.id && Objects.equals(taskList, manager.taskList)
                && Objects.equals(epicList, manager.epicList)
                && Objects.equals(scanner, manager.scanner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskList, epicList, id, scanner);
    }

    @Override
    public String toString() {
        return "Manager{" + "\n\t" +
                "taskList=" + "\n\t" + taskList.entrySet() + "\n\t" +
                "epicList=" + "\n\t" + epicList + '}' + "\n\t";
    }

}
