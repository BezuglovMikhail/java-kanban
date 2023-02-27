package ru.yandex.practicum.project.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Calendar.FEBRUARY;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.NameTask;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

public class KVServer {
    public static final int PORT = 8078;
    private static final String SAVE = "/save/";
    private static final String LOAD = "/load/";
    private static final String REGISTER = "/register";
    protected final String apiToken;
    protected final HttpServer server;
    public Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext(REGISTER, this::register);
        server.createContext(SAVE, this::save);
        server.createContext(LOAD, this::load);

        /*Task task1 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15));

        Epic epic1 = new Epic("Переезд", "Переезд в другой город",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(95));

        Subtask subtask1 = new Subtask("Составить список", "Список вещей для переезда",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15));
        Subtask subtask2 =(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15)));

        Task task2 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String task = gson.toJson(task1);
        String task22 = gson.toJson(task2);
        String epic = gson.toJson(epic1);
        String subtaskS1 = gson.toJson(subtask1);
        String subtaskS2 = gson.toJson(subtask2);
        data.put("task", task + "//" + task22 + "//");
        data.put("epic", epic + "//");
        data.put("subtask", subtaskS1 + "//" + subtaskS2 + "//");

        Task taskForHistory = new Task("Прогулка", "Одеться и пойти гулять", Status.NEW, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        String history1 = gson.toJson(taskForHistory);

        Epic epicHistory = new Epic("Переезд", "Переезд в другой город", Status.NEW, 3,
                new ArrayList<>(List.of(4, 5)),
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30),
                Duration.ofMinutes(75), LocalDateTime.of(2023, Month.FEBRUARY, 13, 22, 05), NameTask.EPIC);

        String history2 = gson.toJson(epicHistory);

       Subtask subtaskHistoryTest = new Subtask("Упаковать вещи",
               "Компактно упаковать вещи в коробки и промаркировать", Status.NEW, 5,
               LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15), 3,
               LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.SUBTASK);

        String history3 = gson.toJson(subtaskHistoryTest);
        data.put("history", history1 + "//" + history2 + "//" + history3 + "//");*/
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n" + LOAD);
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring(LOAD.length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                System.out.println("Значение для ключа " + key + " успешно получены!");
                h.sendResponseHeaders(200, 0);
                OutputStream outputStream = h.getResponseBody();
                String taskJson = data.get(key);
                outputStream.write(taskJson.getBytes(StandardCharsets.UTF_8));
            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n" + SAVE);
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring(SAVE.length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n" + REGISTER);
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }


    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}