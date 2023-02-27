package ru.yandex.practicum.project.handle;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.project.manager.HttpTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicSubtasksHandler extends TaskHandler {
    public EpicSubtasksHandler(HttpTaskManager httpTaskManager) throws IOException {
        super(httpTaskManager);
    }

    @Override
    public void switchHandler(String requestMethod, OutputStream outputStream,
                              HttpExchange exchange) throws IOException {
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
