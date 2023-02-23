package ru.yandex.practicum.project.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient kvServerClient = HttpClient.newHttpClient();
    private final String API_TOKEN;
    //private final URI uri;
    //private Gson gson;
    //public KVTaskClient(URL url) {
      //  this.url = url;

    public KVTaskClient(String url) throws IOException, InterruptedException {
            URI uri = URI.create(url);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("Content-type", "application/json")
                    .uri(uri)
                    .build();

        HttpResponse<String> apiToken =
                    kvServerClient.send(request,  HttpResponse.BodyHandlers.ofString());
            API_TOKEN = apiToken.body();
    }

    public void put (String key, String json) {
        URI tasksUri = URI.create("http://localhost:8078/save/" + key + "?API_TOKEN=" + API_TOKEN);
        //URI epicUri = URI.create("http://localhost:8078/save/epics?API_TOKEN=" + API_TOKEN);
       // URI subtasksUri = URI.create("http://localhost:8078/save/subtasks?API_TOKEN=" + API_TOKEN);

        // и для истории
        //InputStream inputStream = h.getRequestBody();
        //String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        //Task task = gson.fromJson(body, Task.class);

        HttpRequest request = HttpRequest.newBuilder()
        //.POST(HttpRequest.BodyPublishers.ofString("{}"))
        .POST(HttpRequest.BodyPublishers.ofString(json)) // тело запроса - все задачи в формате json: "[{"id":1}, {"id":2}]"
        .uri(tasksUri)
        .build();
        // + запросы для эпиков, подзадач и истории

       try {
         getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
         System.out.println("Не могу получить данные от kvserver");
         }
    }

    public String load (String key) {
        //URI uri = URI.create("http://localhost:8078/save/" + key + "?API_TOKEN=" + API_TOKEN);
        URI tasksUri = URI.create("http://localhost:8078/load/tasks?API_TOKEN=" + API_TOKEN);
        URI epicUri = URI.create("http://localhost:8078/load/epics?API_TOKEN=" + API_TOKEN);
        URI subtasksUri = URI.create("http://localhost:8078/load/subtasks?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type", "application/json")
                .uri(tasksUri)
                .build();
        try {
            HttpResponse<String> response = getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
            //HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Не могу получить данные от kvserver");
        }

        // делаем запросы к kvserver, получаем от него задачи в формате json и складываем их в хэш мапы
        //return null;




        return key;
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public HttpClient getKvServerClient() {
        return kvServerClient;
    }
}
