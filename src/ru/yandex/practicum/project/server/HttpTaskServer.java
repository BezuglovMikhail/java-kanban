package ru.yandex.practicum.project.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.project.handle.*;
import ru.yandex.practicum.project.manager.FileBackedTasksManager;
import ru.yandex.practicum.project.manager.HttpTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;


public class HttpTaskServer {

    public HttpTaskServer() throws IOException, InterruptedException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks/task", new TaskHandler(new HttpTaskManager("http://localhost:8078/register")));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(new HttpTaskManager("http://localhost:8078/register")));
        httpServer.createContext("/tasks/epic", new EpicHandler(new HttpTaskManager("http://localhost:8078/register")));
        httpServer.createContext("/tasks/subtask/epic", new EpicSubtasksHandler(new HttpTaskManager("http://localhost:8078/register")));
        httpServer.createContext("/tasks/history", new HistoryHandler(new HttpTaskManager("http://localhost:8078/register")));
        httpServer.createContext("/tasks", new PrioritizedTasksHandler(new HttpTaskManager("http://localhost:8078/register")));

        //httpServer.createContext("/tasks/task/?id=", new TaskHandler(new FileBackedTasksManager("resources/taskAndHistoryTask.csv")));
        httpServer.start();
    }
}
