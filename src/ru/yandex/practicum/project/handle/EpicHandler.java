package ru.yandex.practicum.project.handle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler implements HttpHandler {

        private final HttpTaskManager httpTaskManager;
        private final Gson gson;

        public EpicHandler(HttpTaskManager httpTaskManager) throws IOException {

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
                case "POST": {
                    try {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(body, Epic.class);
                        Type listOfSubtaskObject = new TypeToken<ArrayList<Subtask>>() {}.getType();
                        ArrayList<String> subtasks = gson.fromJson(body, listOfSubtaskObject);
                        if (epic.getNameTask().isEmpty() || epic.getDescription().isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            outputStream.write(("Нельзя добавить задачу без имени и описания.").getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            if (httpTaskManager.getEpicList().containsKey(epic.getId())) {
                                httpTaskManager.updateTask(epic);
                            } else {
                                httpTaskManager.addTask(epic);
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
                            Optional<Integer> epicId;
                            try {
                                epicId = Optional.of(Integer.parseInt(pathParts[1]));
                            } catch (NumberFormatException exception) {
                                epicId = Optional.empty();
                            }
                            if (epicId.isPresent()) {
                                int id = epicId.get();
                                if (httpTaskManager.getEpicList().containsKey(id)) {
                                    httpTaskManager.findTaskIdAndRemove(epicId.get());
                                    exchange.sendResponseHeaders(200, 0);
                                    outputStream.write(("Задача с id = " + epicId.get() + " удалена").getBytes(StandardCharsets.UTF_8));
                                    exchange.close();
                                } else {
                                    exchange.sendResponseHeaders(405, 0);
                                    outputStream.write(("Эпикка с id= " + id + " несуществует").getBytes(StandardCharsets.UTF_8));
                                    exchange.close();
                                }
                            }
                            break;
                        }
                    }
                case "GET": {
                    if (exchange.getRequestURI().getQuery() != null) {
                        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
                        Optional<Integer> epicId;
                        try {
                            epicId = Optional.of(Integer.parseInt(pathParts[1]));
                        } catch (NumberFormatException exception) {
                            epicId = Optional.empty();
                        }
                        if (epicId.isPresent()) {
                            int id = epicId.get();
                            if (httpTaskManager.getEpicList().containsKey(id)) {
                                String taskJson = gson.toJson(httpTaskManager.findTaskId(id));
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
