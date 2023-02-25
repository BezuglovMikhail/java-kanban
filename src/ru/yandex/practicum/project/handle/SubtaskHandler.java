package ru.yandex.practicum.project.handle;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.manager.FileBackedTasksManager;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final Gson gson;

    public SubtaskHandler(HttpTaskManager httpTaskManager) throws IOException {

        this.httpTaskManager = httpTaskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();

        /*Task task1 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15));
        httpTaskManager.addTask(task1);

        Task task2 = new Task("Прогулка1", "Одеться и пойти гулять2",
                LocalDateTime.of(2023, FEBRUARY, 13, 23, 30), Duration.ofMinutes(15));
        httpTaskManager.addTask(task2);*/
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        OutputStream outputStream = exchange.getResponseBody();

        switch (requestMethod) {
            case "DELETE": {
                if (exchange.getRequestURI().getQuery() != null) {
                    String[] pathParts = exchange.getRequestURI().getQuery().split("=");
                    Optional<Integer> taskId;
                    try {
                        taskId = Optional.of(Integer.parseInt(pathParts[1]));
                    } catch (NumberFormatException exception) {
                        taskId = Optional.empty();
                    }
                    if (taskId.isPresent()) {
                        int id = taskId.get();
                        if (httpTaskManager.getSubtaskList().containsKey(id)) {
                            httpTaskManager.findTaskIdAndRemove(taskId.get());
                            exchange.sendResponseHeaders(200, 0);
                            outputStream.write(("Задача с id = " + taskId.get() + " удалена").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Подзадачи с id= " + id + " несуществует").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }
                    }
                    break;
                }
            }
            case "GET": {
                if (exchange.getRequestURI().getQuery() != null) {
                    String[] pathParts = exchange.getRequestURI().getQuery().split("=");
                    Optional<Integer> taskId;
                    try {
                        taskId = Optional.of(Integer.parseInt(pathParts[1]));
                    } catch (NumberFormatException exception) {
                        taskId = Optional.empty();
                    }
                    if (taskId.isPresent()) {
                        int id = taskId.get();
                        if (httpTaskManager.getSubtaskList().containsKey(id)) {
                            Subtask subtaskFound = (Subtask) httpTaskManager.findTaskId(id);
                            String taskJson = gson.toJson(subtaskFound);
                            exchange.sendResponseHeaders(200, 0);
                            outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Подзадачи с id = " + id + " несуществует.").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }

                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        outputStream.write(("Значение id не может быть пустым").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    }
                } else {
                        String taskJson = gson.toJson(httpTaskManager.getSubtaskList());
                        exchange.sendResponseHeaders(200, 0);
                        outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    }
                break;
            }
            default:
                exchange.sendResponseHeaders(405, 0);
                throw new RuntimeException();
        }
    }
}