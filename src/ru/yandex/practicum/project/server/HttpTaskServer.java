package ru.yandex.practicum.project.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.project.handle.*;
import ru.yandex.practicum.project.manager.FileBackedTasksManager;
import ru.yandex.practicum.project.manager.HttpTaskManager;
import ru.yandex.practicum.project.manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;


public class HttpTaskServer {
    private HttpTaskManager httpTaskManager;
    private HttpServer httpServer;
    public HttpTaskServer() throws IOException, InterruptedException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpTaskManager = (HttpTaskManager) Managers.getDefault();
        httpServer.createContext("/tasks/task", new TaskHandler(httpTaskManager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(httpTaskManager));
        httpServer.createContext("/tasks/epic", new EpicHandler(httpTaskManager));
        httpServer.createContext("/tasks/subtask/epic", new EpicSubtasksHandler(httpTaskManager));
        httpServer.createContext("/tasks/history", new HistoryHandler(httpTaskManager));
        httpServer.createContext("/tasks", new PrioritizedTasksHandler(httpTaskManager));
        httpServer.start();
    }

    public HttpTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    public void stop() {
        httpServer.stop(0);
    }
}
