package ru.yandex.practicum.project.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.server.KVTaskClient;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static ru.yandex.practicum.project.status.Status.NEW;

public class HttpTaskManager extends FileBackedTasksManager {
    //private final HttpClient kvServerClient;
    private final String API_TOKEN;
    private final KVTaskClient kvTaskClient;
    private Gson gson;
    private final String urlServes = "http://localhost:8078/register";

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        loadFromServer();
        ///URI uri = URI.create("http://localhost:8078/register");
       /* URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type", "application/json")
                .uri(uri)
                .build();

        HttpResponse<String> apiToken =
                kvServerClient.send(request,  HttpResponse.BodyHandlers.ofString());
        API_TOKEN = apiToken.body();*/
        kvTaskClient = new KVTaskClient(url);
        this.API_TOKEN = kvTaskClient.getAPI_TOKEN();
        load("http://localhost:8078/");
    }

    // переопределяем метод save. Раньше он сохранял данные в файл, а теперь - на kvserver
    // для этого делаем запросы к kvserver на сохранение данных по ключам tasks, epics, subtasks и history
    // таким образом на сервере в хэш мапе будут храниться данные по ключам tasks, epics, subtasks и history,
    // а значения - все задачи/эпики/подзадачи/история в формате json

    //public void save() {
    //kvTaskClient.put();
    // URI tasksUri = URI.create("http://localhost:8078/save/tasks?API_TOKEN=" + API_TOKEN);
    //URI epicUri = URI.create("http://localhost:8078/save/epics?API_TOKEN=" + API_TOKEN);
    //URI subtasksUri = URI.create("http://localhost:8078/save/subtasks?API_TOKEN=" + API_TOKEN);
    // и для истории
    //InputStream inputStream = h.getRequestBody();
    //String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    //Task task = gson.fromJson(body, Task.class);

    //HttpRequest request = HttpRequest.newBuilder()
    //.POST(HttpRequest.BodyPublishers.ofString("{}"))
    //.POST(HttpRequest.BodyPublishers.ofString(task.toString())) // тело запроса - все задачи в формате json: "[{"id":1}, {"id":2}]"
    //.uri(tasksUri)
    //.build();
    // + запросы для эпиков, подзадач и истории

    //try {
    // kvTaskClient.getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
    //} catch (IOException | InterruptedException e) {
    // System.out.println("Не могу получить данные от kvserver");
    // }
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
            historyString.append(task.getId()).append(",");
        }

        kvTaskClient.put("task", taskString.toString());
        kvTaskClient.put("epic", epicString.toString());
        kvTaskClient.put("subtask", subtaskString.toString());
        kvTaskClient.put("history", historyString.toString());

               /* for (Epic epic : getEpicList().values()) {
                    bw.write(toString(epic));
                    bw.newLine();
                }
                for (Subtask subtask : getSubtaskList().values()) {
                    bw.write(toString(subtask));
                    bw.newLine();
                }
                if (historyManager.getHistory().size() != 0) {
                    bw.newLine();
                    bw.write(historyToString(historyManager));
                }*/

        // try {
        //  kvTaskClient.getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
        // } catch (IOException | InterruptedException e) {
        // System.out.println("Не могу получить данные от kvserver");
        // }


    }

    //@Override
    public HttpTaskManager load(String urlServes) throws IOException, InterruptedException {



       // kvTaskClient.load("task");
        //kvTaskClient.load("epic");
        //kvTaskClient.load("subtask");
        //kvTaskClient.load("history");

        String[] tasksString = kvTaskClient.load("task").split("//");

        for (int i = 0;  i < tasksString.length; i++  ) {
            if (!tasksString[i].isEmpty()) {
                Task task = gson.fromJson(tasksString[i], Task.class);
                addTask(task);
            }
        }


        String[] epicString = kvTaskClient.load("epic").split("//");
        String[] subtaskString =  kvTaskClient.load("subtask").split("//");
        String[] historyString = kvTaskClient.load("history").split("//");



        /*URI tasksUri = URI.create("http://localhost:8078/load/tasks?API_TOKEN=" + API_TOKEN);
        URI epicUri = URI.create("http://localhost:8078/load/epics?API_TOKEN=" + API_TOKEN);
        URI subtasksUri = URI.create("http://localhost:8078/load/subtasks?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type", "application/json")
                .uri(tasksUri)
                .build();
        try {
            HttpResponse<String> response = kvTaskClient.getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
            //HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Не могу получить данные от kvserver");
        }*/

    // делаем запросы к kvserver, получаем от него задачи в формате json и складываем их в хэш мапы
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
        return epic;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.updateEpic(epic, subtasks);
        save();
        return epic;
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        super.findTaskId(taskId);
        historyToString(historyManager);
        save();
        return getTaskList().get(taskId);
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        super.findSubtaskForEpicId(epicId);
        historyToString(historyManager);
        save();
        return null;
    }

    @Override
    public void printAllTask() throws IOException {
        if (getEpicList().size() != 0 || getSubtaskList().size() != 0 || getTaskList().size() != 0) {
            super.printAllTask();
            historyToString(historyManager);
            save();
        }
    }

    @Override
    public void findTaskIdAndRemove(int taskId) throws IOException {
        super.findTaskIdAndRemove(taskId);
        historyToString(historyManager);
        save();
    }

    /*static String historyToString(HistoryManager<Task> historyManager) {
        StringBuilder historyToStringId = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            historyToStringId.append(task.getId()).append(",");
        }
        return historyToStringId.toString();
    }*/


    /*
     public static FileBackedTasksManager load(String fileName) throws IllegalAccessException, IOException {
        FileBackedTasksManager fileBackedTasksManager = null;
        File file = new File(fileName);
        if (file.length() != 0) {
            ArrayList<String> fileBacked = reader(file);
            String fileBackedHistory = "";
            if (fileBacked.get(fileBacked.size() - 2).isEmpty()) {
                fileBackedHistory = fileBacked.get(fileBacked.size() - 1);
            }
            String fileNew = "resources/newFileBackedTaskManager.csv";
            fileBackedTasksManager = new FileBackedTasksManager(fileNew);
            fileBacked.remove(0);
            if (!Objects.equals(fileBackedHistory, "")) {
                fileBacked.remove(fileBacked.size() - 1);
                fileBacked.remove(fileBacked.size() - 1);
            }
            for (String taskLine : fileBacked) {
                String[] line = taskLine.split(",");
                NameTask type = NameTask.valueOf(line[1]);
                switch (type) {
                    case TASK: {
                        int taskId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getTaskList().put(taskId,
                                fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case EPIC: {
                        int epicId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getEpicList().put(epicId,
                                (Epic) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case SUBTASK: {
                        int subtaskId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getSubtaskList().put(subtaskId,
                                (Subtask) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    default:
                        System.out.println("неверный тип задачи: " + type);
                        throw new IllegalAccessException();
                }
            }
            for (Integer taskId : historyFromString(fileBackedHistory)) {
                fileBackedTasksManager.findTaskId(taskId);
            }
            fileBackedTasksManager.save();
        }
        return fileBackedTasksManager;
    }
     */


    public void loadFromServer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();

        //String json = kvServerClient.load(...);
        //gson.fromJson(json, ...);
// и так ещё три раза
    }





   /* В клиенте:
    public String load(String key) {
        HttpResponse<String> response = client.send(...);
        return response.body();
    }*/

    public KVTaskClient getKVTaskClient() {
        return kvTaskClient;
    }
}