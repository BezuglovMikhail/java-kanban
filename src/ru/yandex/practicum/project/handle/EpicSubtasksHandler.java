package ru.yandex.practicum.project.handle;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicSubtasksHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final Gson gson;

    public EpicSubtasksHandler(HttpTaskManager httpTaskManager) throws IOException {

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

        if ("GET".equals(requestMethod)) {
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
                    String taskJson = gson.toJson(httpTaskManager.findSubtaskForEpicId(id));
                    exchange.sendResponseHeaders(200, 0);
                    outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                    exchange.close();
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    outputStream.write(("Эпика с id = " + id + " несуществует.").getBytes(StandardCharsets.UTF_8));
                    exchange.close();
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                outputStream.write(("Значение id не может быть пустым").getBytes(StandardCharsets.UTF_8));
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            throw new RuntimeException();
        }
    }

}
