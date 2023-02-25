package ru.yandex.practicum.project.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.server.KVTaskClient;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTasksManager {
    private final String API_TOKEN;
    private final KVTaskClient kvTaskClient;
    private Gson gson;
    private final String urlServes = "http://localhost:8078/register";

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        loadFromServer();
        kvTaskClient = new KVTaskClient(url);
        this.API_TOKEN = kvTaskClient.getAPI_TOKEN();
        load("http://localhost:8078/");
    }

    @Override
    public void save() {
        StringBuilder taskString = new StringBuilder();
        StringBuilder epicString = new StringBuilder();
        StringBuilder subtaskString = new StringBuilder();
        StringBuilder historyString = new StringBuilder();
        for (Task task : getTaskList().values()) {
            String taskJson = gson.toJson(task);
            taskString.append(taskJson).append("//");
        }
        for (Epic epic : getEpicList().values()) {
            String epicJson = gson.toJson(epic);
            epicString.append(epicJson).append("//");
        }
        for (Subtask subtask : getSubtaskList().values()) {
            String epicJson = gson.toJson(subtask);
            subtaskString.append(epicJson).append("//");
        }
        for (Task task : historyManager.getHistory()) {
            String taskJson = gson.toJson(task);
            historyString.append(taskJson).append("//");
        }
        kvTaskClient.put("task", taskString.toString());
        kvTaskClient.put("epic", epicString.toString());
        kvTaskClient.put("subtask", subtaskString.toString());
        kvTaskClient.put("history", historyString.toString());
    }

    public HttpTaskManager load(String urlServes) throws IOException, InterruptedException {

        String[] tasksString = kvTaskClient.load("task").split("//");

        for (String s : tasksString) {
            if (!s.isEmpty()) {
                Task task = gson.fromJson(s, Task.class);
                addTask(task);
            }
        }
        String[] epicString = kvTaskClient.load("epic").split("//");
        String[] subtaskString = kvTaskClient.load("subtask").split("//");

        for (String sEpic : epicString) {
            if (!sEpic.isEmpty()) {
                Epic epic = gson.fromJson(sEpic, Epic.class);
                ArrayList<Subtask> subtasks = new ArrayList<>();
                for (String sSubtask : subtaskString) {
                    Subtask subtask = gson.fromJson(sSubtask, Subtask.class);
                    subtasks.add(subtask);
                }
                addEpic(epic, subtasks);
            }
        }

        String[] historyString = kvTaskClient.load("history").split("//");
        for (String s : historyString) {
            if (!s.isEmpty()) {
                if (s.contains("TASK")) {
                    Task task = gson.fromJson(s, Task.class);
                    historyManager.add(task);
                }
                if (s.contains("EPIC")) {
                    Epic epic = gson.fromJson(s, Epic.class);
                    historyManager.add(epic);
                }
                if (s.contains("SUBTASK")) {
                    Subtask subtask = gson.fromJson(s, Subtask.class);
                    historyManager.add(subtask);
                }
            }
        }
        return null;
    }

    @Override
    public Task addTask(Task task) throws IOException {
        super.addTask(task);
        save();
        return getTaskList().get(task.getId());
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.addEpic(epic, subtasks);
        save();
        return getEpicList().get(epic.getId());
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
        return getTaskList().get(task.getId());
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.updateEpic(epic, subtasks);
        save();
        return getEpicList().get(epic.getId());
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        Task taskFound = super.findTaskId(taskId);
        save();
        return taskFound;
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        ArrayList<Subtask> subtasksForEpicId = super.findSubtaskForEpicId(epicId);
        save();
        return subtasksForEpicId;
    }

    @Override
    public void printAllTask() throws IOException {
        if (getEpicList().size() != 0 || getSubtaskList().size() != 0 || getTaskList().size() != 0) {
            super.printAllTask();
            save();
        }
    }

    @Override
    public void findTaskIdAndRemove(int taskId) throws IOException {
        super.findTaskIdAndRemove(taskId);
        save();
    }

    public void loadFromServer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }
}