package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.Epic;
import ru.yandex.practicum.project.task.NameTask;
import ru.yandex.practicum.project.task.Subtask;
import ru.yandex.practicum.project.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ru.yandex.practicum.project.status.Status.*;
import static ru.yandex.practicum.project.task.NameTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String fileName;
    private final HashMap<Integer, String> allTask = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> epicSubtaskId = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> epicSubtask = new HashMap<>();
    private final HashMap<Integer, Task> fileBackedTask = new HashMap<>();
    private final HashMap<Integer, Epic> fileBackedEpic = new HashMap<>();

    private FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public void save() throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName), StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,type,name,status,description,epic");

            for (String task : allTask.values()) {
                bw.write("\n" + task);
            }
            if (historyManager.getHistory().size() != 0) {
                bw.write("\n\n" + historyToString(historyManager) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка ввода выода", e);
        }
    }

    static String historyToString(HistoryManager<Task> historyManager) {
        StringBuilder historyToStringId = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            historyToStringId.append(task.getId()).append(",");
        }
        return historyToStringId.toString();
    }

    public <T extends Task> Object fromString(String value) throws IllegalAccessException {
        String[] task = value.split(",");
        int id = Integer.parseInt(task[0]);
        String type = task[1];
        String name = task[2];
        String status = task[3];
        String description = task[4];
        switch (type) {
            case "TASK": {
                return new Task(name, description, status, id);
            }
            case "EPIC": {
                return new Epic(name, description, status, id, epicSubtaskId.get(id));
            }
            case "SUBTASK": {
                int epic = Integer.parseInt(task[5]);
                Subtask subtask = new Subtask(name, description, status, id, epic);
                return subtask;
            }
            default:
                System.out.println("неверный тип задачи: " + type);
                throw new IllegalAccessException();
        }
    }

    public static List<Integer> historyFromString(String value) {
        ArrayList<Integer> historyTask = new ArrayList<>();
        if (value != null) {
            String[] historyString = value.split(",");
            for (String taskId : historyString) {
                if (taskId != null) {
                    historyTask.add(Integer.parseInt(taskId));
                }
            }
        }
        return historyTask;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IllegalAccessException, IOException {
        ArrayList<String> fileBacked = new ArrayList<>();
        String fileBackedHistory;
        String fileNew = "resources/newFileBackedTaskManager.csv";
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(fileNew);
        if (file.length() != 0) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    fileBacked.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileBackedHistory = fileBacked.get(fileBacked.size() - 1);
            fileBacked.remove(0);
            fileBacked.remove(fileBacked.size() - 1);
            fileBacked.remove(fileBacked.size() - 1);
            for (String taskLine : fileBacked) {
                String[] line = taskLine.split(",");
                String type = line[1];

                switch (type) {
                    case "TASK": {
                        int taskId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.fileBackedTask.put(taskId,
                                (Task) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case "EPIC": {
                        int epicId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.fileBackedEpic.put(epicId,
                                (Epic) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case "SUBTASK": {
                        int epic = Integer.parseInt(line[5]);
                        if (fileBackedTasksManager.epicSubtask.containsKey(epic)) {
                            fileBackedTasksManager.epicSubtask.get(epic).
                                    add((Subtask) fileBackedTasksManager.fromString(taskLine));
                        } else {
                            fileBackedTasksManager.epicSubtask.put(epic,
                                    new ArrayList<>(List.of((Subtask) fileBackedTasksManager.fromString(taskLine))));
                        }
                        break;
                    }
                    default:
                        System.out.println("неверный тип задачи: " + type);
                        throw new IllegalAccessException();
                }
            }

            for (Task task : fileBackedTasksManager.fileBackedTask.values()) {
                String status = task.getStatus();
                fileBackedTasksManager.addTask(task);
                fileBackedTasksManager.getTaskList().get(task.getId()).setStatus(status);
                fileBackedTasksManager.updateTask(task);
            }

            for (Epic epic : fileBackedTasksManager.fileBackedEpic.values()) {

                for (Subtask subtask : fileBackedTasksManager.epicSubtask.get(epic.getId())) {
                    fileBackedTasksManager.getSubtaskList().put(subtask.getId(), subtask);
                }

                if (epic.getStatus().equals(String.valueOf(NEW))) {
                    fileBackedTasksManager.addEpic(epic, fileBackedTasksManager.epicSubtask.get(epic.getId()));
                }
                fileBackedTasksManager.updateEpic(epic, fileBackedTasksManager.epicSubtask.get(epic.getId()));
            }

            if (fileBacked.size() > 1) {
                ArrayList<Integer> historyManager = new ArrayList<>(historyFromString(fileBackedHistory));

                for (int taskId : historyManager) {
                    fileBackedTasksManager.findTaskId(taskId);
                }
            }
        }
        return fileBackedTasksManager;
    }

    public String toString(Task task) {
        return task.getId() + "," +
                TASK + "," +
                task.getNameTask() + "," +
                task.getStatus() + "," +
                task.getDescription();
    }

    public String toString(Epic epic) {
        return epic.getId() + "," +
                NameTask.EPIC + "," +
                epic.getNameTask() + "," +
                epic.getStatus() + "," +
                epic.getDescription();
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," +
                NameTask.SUBTASK + "," +
                subtask.getNameTask() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getIdEpic();
    }

    @Override
    public Task addTask(Task task) throws IOException {
        super.addTask(task);
        String taskString = toString(task);
        String[] taskId = taskString.split(",", 2);
        this.allTask.put(Integer.parseInt(taskId[0]), taskString);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.addEpic(epic, subtasks);
        String epicString = toString(epic);
        String[] epicId = epicString.split(",", 2);
        this.allTask.put(Integer.parseInt(epicId[0]), toString(epic));
        this.epicSubtaskId.put(epic.getId(), epic.getIdSubtaskEpic());
        for (Subtask subtask : subtasks) {
            String subtaskString = toString(subtask);
            String[] subtaskId = subtaskString.split(",", 2);
            this.allTask.put(Integer.parseInt(subtaskId[0]), toString(subtask));
        }
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        super.updateTask(task);
        String taskString = toString(task);
        String[] taskId = taskString.split(",", 2);
        this.allTask.put(Integer.parseInt(taskId[0]), taskString);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.updateEpic(epic, subtasks);
        String epicString = toString(epic);
        String[] epicId = epicString.split(",", 2);
        this.allTask.put(Integer.parseInt(epicId[0]), epicString);
        for (Subtask subtask : subtasks) {
            String subtaskString = toString(subtask);
            String[] subtaskId = subtaskString.split(",", 2);
            this.allTask.put(Integer.parseInt(subtaskId[0]), subtaskString);
        }
        save();
        return epic;
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        super.findTaskId(taskId);
        historyToString(historyManager);
        save();
        return null;
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        super.findSubtaskForEpicId(epicId);
        historyToString(historyManager);
        save();
        return null;
    }

    @Override
    public void printAllTask() throws IOException {
        super.printAllTask();
        historyToString(historyManager);
        save();
    }

    public static void main(String[] args) throws IOException, IllegalAccessException {
        FileBackedTasksManager fileBackedTasksManager =
                new FileBackedTasksManager("resources/taskAndHistoryTask.csv");
        loadFromFile(new File("resources/taskAndHistoryTask.csv"));

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять");
        Epic epic1 = new Epic("Переезд", "Переезд в другой город");
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Составить список", "Список вещей для переезда"));
        subtasks1.add(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать"));
        Epic epic2 = new Epic("Прогулка с детьми", "Собрать детей взять снегокат и пойти гулять");
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Одеть детей", "Поймать и одеть детей"));

        fileBackedTasksManager.addTask(task1);
        //fileBackedTasksManager.printAllTask();
        fileBackedTasksManager.addEpic(epic1, subtasks1);
        fileBackedTasksManager.addEpic(epic2, subtasks2);
        fileBackedTasksManager.findTaskId(5);
        fileBackedTasksManager.findTaskId(3);
        fileBackedTasksManager.findTaskId(2);
        fileBackedTasksManager.findTaskId(1);

        fileBackedTasksManager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), String.valueOf(DONE),
                task1.getId()));
        fileBackedTasksManager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), String.valueOf(DONE),
                        epic2.getId(), epic2.getIdSubtaskEpic())),
                new ArrayList<>(Collections.singleton
                        (new Subtask(fileBackedTasksManager.getSubtaskList().get(6).getNameTask(),
                                fileBackedTasksManager.getSubtaskList().get(6).getDescription(),
                                String.valueOf(IN_PROGRESS),
                                fileBackedTasksManager.getSubtaskList().get(6).getId(),
                                fileBackedTasksManager.getSubtaskList().get(6).getIdEpic()))));

        System.out.println(fileBackedTasksManager.historyManager.getHistory());

        //System.out.println(fileBackedTasksManager.historyToStringList);
        //fileBackedTasksManager.printAllTask();
        //fileBackedTasksManager.findTaskId(3);
        //fileBackedTasksManager.findTaskId(1);
        //fileBackedTasksManager.printAllTask();
        //System.out.println(fileBackedTasksManager.historyToStringList);
    }
}


