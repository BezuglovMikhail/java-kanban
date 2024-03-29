package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {

    Task addTask(Task task) throws IOException;

    Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException;

    Task updateTask(Task task) throws IOException;

    Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException;

    Task findTaskId(int taskId) throws IOException;

    TreeSet<Task> getPrioritizedTasks();

    boolean checkCrossTask(Task task, TreeSet<Task> prioritizedTasks);

    void findTaskIdAndRemove(int taskId) throws IOException;

    ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException;

    void cleanTask() throws IOException;

    void printAllTask() throws IOException;
}
