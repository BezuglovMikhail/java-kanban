package ru.yandex.practicum.project.handle;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.task.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends TaskHandler {
    public SubtaskHandler(HttpTaskManager httpTaskManager) throws IOException {
        super(httpTaskManager);
    }

    @Override
    public void switchHandler(String requestMethod, OutputStream outputStream,
                              HttpExchange exchange) throws IOException {
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
                            outputStream.write(("Задача с id = " + taskId.get() + " удалена")
                                    .getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Подзадачи с id= " + id + " несуществует")
                                    .getBytes(StandardCharsets.UTF_8));
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
                            outputStream.write(("Подзадачи с id = " + id + " несуществует.")
                                    .getBytes(StandardCharsets.UTF_8));
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