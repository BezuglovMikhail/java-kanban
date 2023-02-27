package ru.yandex.practicum.project.handle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends TaskHandler {
    public EpicHandler(HttpTaskManager httpTaskManager) throws IOException {
        super(httpTaskManager);
    }

    @Override
    public void switchHandler(String requestMethod, OutputStream outputStream,
                              HttpExchange exchange) throws IOException {
        switch (requestMethod) {
            case "POST": {
                try (InputStream inputStream = exchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Epic epic = gson.fromJson(jsonObject.get("epic").getAsJsonObject(), Epic.class);
                    Type listOfSubtaskObject = new TypeToken<ArrayList<Subtask>>() {
                    }.getType();

                    ArrayList<Subtask> subtasks = gson.fromJson(jsonObject.get("subtasks")
                            .getAsJsonArray(), listOfSubtaskObject);

                    if (epic.getNameTask().isEmpty() || epic.getDescription().isEmpty()) {
                        exchange.sendResponseHeaders(400, 0);
                        outputStream.write(("Нельзя добавить задачу без имени и описания.")
                                .getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    } else {
                        if (httpTaskManager.getEpicList().containsKey(epic.getId())) {
                            httpTaskManager.updateEpic(epic, subtasks);
                        } else {
                            httpTaskManager.addEpic(epic, subtasks);
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
                            outputStream.write(("Задача с id = " + epicId.get() + " удалена")
                                    .getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Эпикка с id= " + id + " несуществует")
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
                    Optional<Integer> epicId;
                    try {
                        epicId = Optional.of(Integer.parseInt(pathParts[1]));
                    } catch (NumberFormatException exception) {
                        epicId = Optional.empty();
                    }
                    if (epicId.isPresent()) {
                        int id = epicId.get();
                        if (httpTaskManager.getEpicList().containsKey(id)) {
                            Epic epicFound = (Epic) httpTaskManager.findTaskId(id);
                            String taskJson = gson.toJson(epicFound);
                            exchange.sendResponseHeaders(200, 0);
                            outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(405, 0);
                            outputStream.write(("Задачи с id = " + id + " несуществует.")
                                    .getBytes(StandardCharsets.UTF_8));
                            exchange.close();
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        outputStream.write(("Значение id не может быть пустым").getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                    }
                } else {
                    String taskJson = gson.toJson(httpTaskManager.getEpicList());
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
	"epic": {
	"nameTask": "Прогулка24",
	"description": "Одеться и пойти гулять24"
	},

"subtasks": [
	{
	"nameTask": "Прогулка2",
	"description": "Одеться и пойти гулять2",
	"duration": 15,
	"startTime": "16-02-2023/13:30"
	},
	{
	"nameTask": "Прогулка2",
	"description": "Одеться и пойти гулять2",
	"duration": 15,
	"startTime": "16-02-2023/14:30"
		}
 ]
}


 */