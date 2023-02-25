package ru.yandex.practicum.project.handle;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.task.Task;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final Gson gson;

    public TaskHandler(HttpTaskManager httpTaskManager) throws IOException {

        this.httpTaskManager = httpTaskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        OutputStream outputStream = exchange.getResponseBody();

        switch (requestMethod) {
            case "POST": {
                try {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getNameTask().isEmpty() || task.getDescription().isEmpty()) {
                        exchange.sendResponseHeaders(400, 0);
                        outputStream.write(("Нельзя добавить задачу без имени и описания.").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    } else {
                        if (httpTaskManager.getTaskList().containsKey(task.getId())) {
                            httpTaskManager.updateTask(task);
                        } else {
                            httpTaskManager.addTask(task);
                        }
                        exchange.sendResponseHeaders(201, 0);
                        outputStream.write(("Задача успешно добавлена.").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        break;
                    }
                } catch (JsonSyntaxException exception) {
                    exchange.sendResponseHeaders(400, 0);
                    outputStream.write(("Получен некорректный JSON.").getBytes(StandardCharsets.UTF_8));
                    exchange.close();
                    break;
                }
            }
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
                        if (httpTaskManager.getTaskList().containsKey(id)) {
                            httpTaskManager.findTaskIdAndRemove(id);
                            exchange.sendResponseHeaders(200, 0);
                            outputStream.write(("Задача с id = " + id + " удалена").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }
                        else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Задачи с id = " + id + " несуществует.").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        outputStream.write(("Значение id не может быть пустым").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    }
                } else {
                    httpTaskManager.cleanTask();
                    exchange.sendResponseHeaders(200, 0);
                    outputStream.write(("Задачи удалены").getBytes(StandardCharsets.UTF_8));
                    exchange.close();
                }
                break;
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
                        if (httpTaskManager.getTaskList().containsKey(id)) {
                            Task taskFound = httpTaskManager.findTaskId(id);
                            String taskJson = gson.toJson(taskFound);
                            exchange.sendResponseHeaders(200, 0);
                            outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Задачи с id = " + id + " несуществует.").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }

                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        outputStream.write(("Значение id не может быть пустым").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    }
                } else {
                    String taskJson = gson.toJson(httpTaskManager.getTaskList());
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
/*
{
	"nameTask": "Прогулка",
	"description": "Одеться и пойти гулять",
	"status": "NEW",
	"id": 1,
	"type": "TASK",
	"duration": 15,
	"startTime": "13--02--2023",
	"endTime": "13--02--2023"
}
 */