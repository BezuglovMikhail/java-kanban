package ru.yandex.practicum.project.handle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.manager.HttpTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedTasksHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(HttpTaskManager httpTaskManager) throws IOException {

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
            if (httpTaskManager.getPrioritizedTasks().isEmpty()) {
                exchange.sendResponseHeaders(405, 0);
                outputStream.write(("Список задач пуст.").getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                String taskJson = gson.toJson(httpTaskManager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            throw new RuntimeException();
        }
    }
}
