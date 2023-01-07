package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic, ArrayList<Subtask> subtasks);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks);

    Task findTaskId(int taskId);

    void findTaskIdAndRemove(int taskId);

    ArrayList<Subtask> findSubtaskForEpicId(int epicId);

    void cleanTask();

    void printAllTask();
}
