package ru.yandex.practicum.project.handle;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.project.manager.HttpTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PrioritizedTasksHandler extends TaskHandler {
    public PrioritizedTasksHandler(HttpTaskManager httpTaskManager) throws IOException {
        super(httpTaskManager);
    }

    @Override
    public void switchHandler(String requestMethod, OutputStream outputStream, HttpExchange exchange) throws IOException {
        if ("GET".equals(requestMethod)) {
            if (httpTaskManager.getPrioritizedTasks().isEmpty()) {
                exchange.sendResponseHeaders(405, 0);
                outputStream.write(("Список задач пуст.").getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                String taskJson = gson.toJson(httpTaskManager.getPrioritizedTasks());
                System.out.println(httpTaskManager.getPrioritizedTasks());
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
