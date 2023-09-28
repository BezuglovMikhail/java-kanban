import ru.yandex.practicum.project.server.HttpTaskServer;
import ru.yandex.practicum.project.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        new HttpTaskServer();
    }
}
