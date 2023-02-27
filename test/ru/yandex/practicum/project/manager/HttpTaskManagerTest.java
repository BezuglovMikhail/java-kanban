package ru.yandex.practicum.project.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.project.adapters.DurationAdapter;
import ru.yandex.practicum.project.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.project.handle.TaskHandler;
import ru.yandex.practicum.project.server.HttpTaskServer;
import ru.yandex.practicum.project.server.KVServer;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;


import static java.util.Calendar.FEBRUARY;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


import ru.yandex.practicum.project.task.NameTask;


class HttpTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    HttpTaskServer taskServer;
    KVServer kvServer;
    Gson gson;

    @Override
    @BeforeEach
    void createTasks() throws IOException, InterruptedException {
        super.create();
        kvServer = new KVServer();
        kvServer.start();

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15));

        Epic epic1 = new Epic("Переезд", "Переезд в другой город",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(95));

        Subtask subtask1 = new Subtask("Составить список", "Список вещей для переезда",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15));
        Subtask subtask2 = (new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать",
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15)));

        Task task2 = new Task("Написание тестов", "Неотвлекаться и писать тесты",
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
        String task = gson.toJson(task1);
        String task22 = gson.toJson(task2);
        String epic = gson.toJson(epic1);
        String subtaskS1 = gson.toJson(subtask1);
        String subtaskS2 = gson.toJson(subtask2);
        kvServer.data.put("task", task + "//" + task22 + "//");
        kvServer.data.put("epic", epic + "//");
        kvServer.data.put("subtask", subtaskS1 + "//" + subtaskS2 + "//");

        Task taskForHistory = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.NEW, 2,
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
        kvServer.data.put("history", history1 + "//" + history2 + "//" + history3 + "//");
        taskServer = new HttpTaskServer();
    }

    @AfterEach
    void stopServer() throws IOException, InterruptedException {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void getTasksTaskTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForTest = new Task("Прогулка", "Одеться и пойти гулять", Status.NEW, 1,
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 45), NameTask.TASK);

        Task taskForTest2 = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.NEW, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        HashMap<Integer, Task> testTask = new HashMap<>();
        testTask.put(1, taskForTest);
        testTask.put(2, taskForTest2);

        String forTest = gson.toJson(testTask);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksTaskForIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForTest2 = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.NEW, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        String forTest = gson.toJson(taskForTest2);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void postTasksTaskAddTaskTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/task/");

        Task taskForTest3 = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15));

        String postForTest = gson.toJson(taskForTest3);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postForTest))
                .uri(url)
                .build();

        URI urlGet = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForGetTest = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.NEW, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        String taskForGetJsonTest = gson.toJson(taskForGetTest);

        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());

            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGet.body());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void postTasksTaskUpdateTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");

        Task taskForUpdateTaskNew = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.NEW, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        String postTaskForUpdateTaskNew = gson.toJson(taskForUpdateTaskNew);

        HttpRequest requestPostNew = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postTaskForUpdateTaskNew))
                .uri(url)
                .build();

        URI urlGetTaskId6New = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestGetTaskId6New = HttpRequest.newBuilder()
                .GET()
                .uri(urlGetTaskId6New)
                .build();

        Task taskForUpdateTaskInProgress = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.IN_PROGRESS, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        Task taskForUpdateTaskDone = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.DONE, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        String postTaskForUpdateTaskInProgress = gson.toJson(taskForUpdateTaskInProgress);

        HttpRequest requestPostInProgress = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postTaskForUpdateTaskInProgress))
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        String postTaskForUpdateTaskDone = gson.toJson(taskForUpdateTaskDone);

        HttpRequest requestPostDone = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postTaskForUpdateTaskDone))
                .uri(url)
                .build();

        URI urlGetTaskId6 = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestGetTaskId6 = HttpRequest.newBuilder()
                .GET()
                .uri(urlGetTaskId6)
                .build();

        URI urlGetTaskId2 = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest requestGetTaskId2 = HttpRequest.newBuilder()
                .GET()
                .uri(urlGetTaskId2)
                .build();

        try {
            HttpResponse<String> responsePostNew = client.send(requestPostNew, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePostNew.statusCode());

            HttpResponse<String> responseGetTaskId6New = client.send(requestGetTaskId6New, HttpResponse.BodyHandlers.ofString());
            assertEquals(postTaskForUpdateTaskNew, responseGetTaskId6New.body());

            HttpResponse<String> responsePostInProgress = client.send(requestPostInProgress, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePostInProgress.statusCode());

            HttpResponse<String> responseGetTaskId6 = client.send(requestGetTaskId6, HttpResponse.BodyHandlers.ofString());
            assertEquals(postTaskForUpdateTaskInProgress, responseGetTaskId6.body());

            HttpResponse<String> responsePostDone = client.send(requestPostDone, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePostDone.statusCode());

            HttpResponse<String> responseGetTaskId2 = client.send(requestGetTaskId2, HttpResponse.BodyHandlers.ofString());
            assertEquals(postTaskForUpdateTaskDone, responseGetTaskId2.body());

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void deleteTasksTaskForIdTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/task/");

        Task taskForTest3 = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15));

        String postForTest = gson.toJson(taskForTest3);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postForTest))
                .uri(url)
                .build();

        URI urlGet = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForGetTest = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.NEW, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        String taskForGetJsonTest = gson.toJson(taskForGetTest);

        URI urlDel = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestDel = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel)
                .build();

        URI urlDel2 = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest requestDel2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel2)
                .build();

        URI urlDel3 = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest requestDel3 = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel3)
                .build();

        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());

            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGet.body());

            HttpResponse<String> responseDel = client.send(requestDel, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с id = 6 удалена", responseDel.body());

            HttpResponse<String> responseDel2 = client.send(requestDel2, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с id = 2 удалена", responseDel2.body());

            HttpResponse<String> responseDel3 = client.send(requestDel3, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задачи с id = 3 несуществует.", responseDel3.body());

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void deleteTasksTaskAllTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");

        Task taskForTest3 = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15));

        String postForTest = gson.toJson(taskForTest3);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postForTest))
                .uri(url)
                .build();

        URI urlGet = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();


        URI urlDel = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestDel = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel)
                .build();

        URI urlGetAfterClean = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestGetAfterClean = HttpRequest.newBuilder()
                .GET()
                .uri(urlGetAfterClean)
                .build();

        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());

            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertFalse(false, String.valueOf(responseGet.body().isEmpty()));

            HttpResponse<String> responseDel = client.send(requestDel, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задачи удалены", responseDel.body());

            HttpResponse<String> responseGetAfterClean = client.send(requestGetAfterClean, HttpResponse.BodyHandlers.ofString());
            assertTrue(true, String.valueOf(responseGet.body().isEmpty()));

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksSubtaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Subtask subtaskId4Test = new Subtask("Составить список",
                "Список вещей для переезда", Status.NEW, 4,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 45), NameTask.SUBTASK);

        Subtask subtaskId5Test = new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать", Status.NEW, 5,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.SUBTASK);

        HashMap<Integer, Subtask> testTask = new HashMap<>();
        testTask.put(4, subtaskId4Test);
        testTask.put(5, subtaskId5Test);

        String forTest = gson.toJson(testTask);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksSubtaskForIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Subtask subtaskId4Test = new Subtask("Составить список",
                "Список вещей для переезда", Status.NEW, 4,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 45), NameTask.SUBTASK);

        String forTest = gson.toJson(subtaskId4Test);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void deleteTasksSubtaskForIdTest() throws IOException, InterruptedException {
        URI urlGet = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Subtask subtaskId4Test = new Subtask("Составить список",
                "Список вещей для переезда", Status.NEW, 4,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 45), NameTask.SUBTASK);

        String forTest = gson.toJson(subtaskId4Test);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        URI urlGetAfter = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest requestGetAfterDel = HttpRequest.newBuilder()
                .GET()
                .uri(urlGetAfter)
                .build();

        try {
            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, responseGet.body());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с id = 4 удалена", response.body());

            HttpResponse<String> responseGetAfterDel = client.send(requestGetAfterDel, HttpResponse.BodyHandlers.ofString());
            assertEquals("Подзадачи с id = 4 несуществует.", responseGetAfterDel.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksEpicTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Epic epicHistory = new Epic("Переезд", "Переезд в другой город", Status.NEW, 3,
                new ArrayList<>(List.of(4, 5)),
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30),
                Duration.ofMinutes(75), LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.EPIC);

        HashMap<Integer, Epic> testEpic = new HashMap<>();
        testEpic.put(3, epicHistory);

        String forTest = gson.toJson(testEpic);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksEpicForIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Epic epicHistory = new Epic("Переезд", "Переезд в другой город", Status.NEW, 3,
                new ArrayList<>(List.of(4, 5)),
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30),
                Duration.ofMinutes(75), LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.EPIC);

        String forTest = gson.toJson(epicHistory);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void postTasksEpicAddEpicTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/epic/");

        Epic epic2 = new Epic("Эпик для теста добавления и обновления эпика", "Описание эпика для теста",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4), Duration.ofMinutes(41));

        Subtask subtask21 = new Subtask("Написать тест", "Тест для проверки добавления и изменения задач",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4), Duration.ofMinutes(15));
        Subtask subtask22 = (new Subtask("Проверить работу тестов",
                "Проверить работу добавления и изменения эпика",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 30), Duration.ofMinutes(15)));

        ArrayList<Subtask> subtasks = new ArrayList<>(List.of(subtask21, subtask22));

        JsonObject epicWithSubtaskJsonObject = new JsonObject();
        JsonObject epicJsonObject = gson.toJsonTree(epic2).getAsJsonObject();
        epicWithSubtaskJsonObject.add("epic", epicJsonObject);
        JsonArray jsonArray = gson.toJsonTree(subtasks).getAsJsonArray();
        epicWithSubtaskJsonObject.add("subtasks", jsonArray);
        String epicWithSubtask = gson.toJson(epicWithSubtaskJsonObject);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicWithSubtask))
                .uri(url)
                .build();

        URI urlGet = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Epic epicAfterAdd = new Epic("Эпик для теста добавления и обновления эпика", "Описание эпика для теста", Status.NEW, 6,
                new ArrayList<>(List.of(7, 8)),
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4),
                Duration.ofMinutes(41), LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 45), NameTask.EPIC);

        String taskForGetJsonTest = gson.toJson(epicAfterAdd);

        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());

            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGet.body());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void postTasksEpicUpdateEpicInProgressTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");

        Epic epic2 = new Epic("Эпик для теста добавления и обновления эпика", "Описание эпика для теста",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4), Duration.ofMinutes(41));

        Subtask subtask21 = new Subtask("Написать тест", "Тест для проверки добавления и изменения задач",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4), Duration.ofMinutes(15));
        Subtask subtask22 = (new Subtask("Проверить работу тестов",
                "Проверить работу добавления и изменения эпика",
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 30), Duration.ofMinutes(15)));

        ArrayList<Subtask> subtasks = new ArrayList<>(List.of(subtask21, subtask22));

        JsonObject epicWithSubtaskJsonObject = new JsonObject();
        JsonObject epicJsonObject = gson.toJsonTree(epic2).getAsJsonObject();
        epicWithSubtaskJsonObject.add("epic", epicJsonObject);
        JsonArray jsonArray = gson.toJsonTree(subtasks).getAsJsonArray();
        epicWithSubtaskJsonObject.add("subtasks", jsonArray);
        String epicWithSubtask = gson.toJson(epicWithSubtaskJsonObject);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicWithSubtask))
                .uri(url)
                .build();

        Epic epicForUpdate = new Epic("Эпик для теста добавления и обновления эпика",
                "Описание эпика для теста", Status.NEW, 6,
                new ArrayList<>(List.of(7, 8)),
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4),
                Duration.ofMinutes(41), LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 45),
                NameTask.EPIC);

        Subtask subtask1ForUpdate = new Subtask("Написать тест",
                "Тест для проверки добавления и изменения задач", Status.DONE, 7,
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4), Duration.ofMinutes(15),
                6, LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 19),
                NameTask.SUBTASK);

        Subtask subtask2ForUpdate = new Subtask("Проверить работу тестов",
                "Проверить работу добавления и изменения эпика", Status.IN_PROGRESS, 8,
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 30), Duration.ofMinutes(15),
                6, LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 45),
                NameTask.SUBTASK);

        ArrayList<Subtask> subtasksForUpdate = new ArrayList<>(List.of(subtask1ForUpdate, subtask2ForUpdate));

        JsonObject epicWithSubtaskForUpdateJsonObject = new JsonObject();
        JsonObject epicForUpdateJsonObject = gson.toJsonTree(epicForUpdate).getAsJsonObject();
        epicWithSubtaskForUpdateJsonObject.add("epic", epicForUpdateJsonObject);
        JsonArray jsonArrayForUpdate = gson.toJsonTree(subtasksForUpdate).getAsJsonArray();
        epicWithSubtaskForUpdateJsonObject.add("subtasks", jsonArrayForUpdate);
        String epicWithSubtaskForUpdate = gson.toJson(epicWithSubtaskForUpdateJsonObject);

        HttpRequest requestPostForUpdate = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicWithSubtaskForUpdate))
                .uri(url)
                .build();

        URI urlGet = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Epic epicAfterUpdate = new Epic("Эпик для теста добавления и обновления эпика",
                "Описание эпика для теста", Status.IN_PROGRESS, 6, new ArrayList<>(List.of(7, 8)),
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 4),
                Duration.ofMinutes(41), LocalDateTime.of(2023, Month.FEBRUARY, 27, 12, 45),
                NameTask.EPIC);

        String taskForGetJsonTest = gson.toJson(epicAfterUpdate);

        try {
            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePost.statusCode());

            HttpResponse<String> responsePostForUpdate = client.send(requestPostForUpdate, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responsePostForUpdate.statusCode());

            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGet.body());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void deleteTasksEpicForIdTest() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/epic/");

        URI urlGet = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("Переезд", "Переезд в другой город", Status.NEW, 3,
                new ArrayList<>(List.of(4, 5)),
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30),
                Duration.ofMinutes(75), LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.EPIC);

        String taskForGetJsonTest = gson.toJson(epic);

        URI urlDel = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest requestDel = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel)
                .build();

        URI urlDel2 = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest requestDel2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel2)
                .build();

        URI urlDel3 = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest requestDel3 = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlDel3)
                .build();

        try {
            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGet.body());

            HttpResponse<String> responseDel = client.send(requestDel, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с id = 3 удалена", responseDel.body());

            HttpResponse<String> responseDel3 = client.send(requestDel3, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задачи с id = 3 несуществует.", responseDel3.body());

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksSubtaskEpicIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Subtask subtaskId4Test = new Subtask("Составить список",
                "Список вещей для переезда", Status.NEW, 4,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 45), NameTask.SUBTASK);

        Subtask subtaskId5Test = new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать", Status.NEW, 5,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.SUBTASK);

        ArrayList<Subtask> testTask = new ArrayList<>();
        testTask.add(subtaskId4Test);
        testTask.add(subtaskId5Test);

        String forTest = gson.toJson(testTask);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(forTest, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void getTasksHistoryTest() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/task/");
        Task taskForTest3 = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15));

        String postForTest = gson.toJson(taskForTest3);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postForTest))
                .uri(urlPost)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForGetTest = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.NEW, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        String taskForGetJsonTest = gson.toJson(taskForGetTest);

        URI urlGet = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestId6 = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        Task taskForHistory = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.NEW, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        Epic epicHistory = new Epic("Переезд", "Переезд в другой город", Status.NEW, 3,
                new ArrayList<>(List.of(4, 5)),
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30),
                Duration.ofMinutes(75), LocalDateTime.of(2023, Month.FEBRUARY, 13, 22, 05), NameTask.EPIC);

        Subtask subtaskHistoryTest = new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать", Status.NEW, 5,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.SUBTASK);

        ArrayList<Task> historyTask = new ArrayList<>();
        historyTask.add(taskForHistory);
        historyTask.add(epicHistory);
        historyTask.add(subtaskHistoryTest);
        historyTask.add(taskForGetTest);
        String history3 = gson.toJson(historyTask);
        try {

            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача успешно добавлена.", responsePost.body());

            HttpResponse<String> responseGetId6 = client.send(requestId6, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGetId6.body());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(history3, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }


    @Test
    void getTasksPrioritizedTasksTest() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks/task/");
        Task taskForTest3 = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15));

        String postForTest = gson.toJson(taskForTest3);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postForTest))
                .uri(urlPost)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        Task taskForGetTest = new Task("Проверка тестов", "Проверить, что тесты тестируют функционал",
                Status.NEW, 6,
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 5, 45), NameTask.TASK);

        String taskForGetJsonTest = gson.toJson(taskForGetTest);

        URI urlGet = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest requestId6 = HttpRequest.newBuilder()
                .GET()
                .uri(urlGet)
                .build();

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        Task taskForHistory = new Task("Написание тестов", "Неотвлекаться и писать тесты", Status.NEW, 2,
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 9, 45), NameTask.TASK);

        Task taskForTest = new Task("Прогулка", "Одеться и пойти гулять", Status.NEW, 1,
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15),
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 45), NameTask.TASK);

        Subtask subtaskId4Test = new Subtask("Составить список",
                "Список вещей для переезда", Status.NEW, 4,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 20, 45), NameTask.SUBTASK);

        Subtask subtaskHistoryTest = new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать", Status.NEW, 5,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 30), Duration.ofMinutes(15), 3,
                LocalDateTime.of(2023, Month.FEBRUARY, 13, 21, 45), NameTask.SUBTASK);

        ArrayList<Task> historyTask = new ArrayList<>();
        historyTask.add(taskForGetTest);
        historyTask.add(taskForHistory);
        historyTask.add(taskForTest);
        historyTask.add(subtaskId4Test);
        historyTask.add(subtaskHistoryTest);
        String history3 = gson.toJson(historyTask);
        try {

            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача успешно добавлена.", responsePost.body());

            HttpResponse<String> responseGetId6 = client.send(requestId6, HttpResponse.BodyHandlers.ofString());
            assertEquals(taskForGetJsonTest, responseGetId6.body());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(history3, response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }










    @Test
    void save() {
    }

    @Test
    void load() {
    }
}