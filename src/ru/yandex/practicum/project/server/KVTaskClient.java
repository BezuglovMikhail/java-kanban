package ru.yandex.practicum.project.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient kvServerClient = HttpClient.newHttpClient();
    private final String API_TOKEN;

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

        HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .uri(tasksUri)
        .build();

       try {
         getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
         System.out.println("Не могу получить данные от kvserver");
         }
    }

    public String load (String key) {
        URI tasksUri = URI.create("http://localhost:8078/load/" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type", "application/json")
                .uri(tasksUri)
                .build();
        try {
            HttpResponse<String> response = getKvServerClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Не могу получить данные от kvserver");
        }
        return key;
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public HttpClient getKvServerClient() {
        return kvServerClient;
    }
}
