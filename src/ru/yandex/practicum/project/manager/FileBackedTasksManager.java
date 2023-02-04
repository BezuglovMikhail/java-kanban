package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.status.Status;
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

    private FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public void save() throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName), StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,type,name,status,description,epic");
            bw.newLine();

            for (Task task : getTaskList().values()) {
                bw.write(toString(task));
                bw.newLine();
            }
            for (Epic epic : getEpicList().values()) {
                bw.write(toString(epic));
                bw.newLine();
            }
            for (Subtask subtask : getSubtaskList().values()) {
                bw.write(toString(subtask));
                bw.newLine();
            }
            if (historyManager.getHistory().size() != 0) {
                bw.newLine();
                bw.write(historyToString(historyManager));
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

    public Task fromString(String value) throws IllegalAccessException {
        String[] task = value.split(",");
        int id = Integer.parseInt(task[0]);
        NameTask type = NameTask.valueOf(task[1]);
        String name = task[2];
        Status status = Status.valueOf(task[3]);
        String description = task[4];
        switch (type) {
            case TASK: {
                return new Task(name, description, status, id);
            }
            case EPIC: {
                return new Epic(name, description, status, id);
            }
            case SUBTASK: {
                int epic = Integer.parseInt(task[5]);
                return new Subtask(name, description, status, id, epic);
            }
            default:
                System.out.println("неверный тип задачи: " + type);
                throw new IllegalAccessException();
        }
    }

    public static List<Integer> historyFromString(String value) {
        ArrayList<Integer> historyTask = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] historyString = value.split(",");
            for (String taskId : historyString) {
                historyTask.add(Integer.parseInt(taskId));
            }
        }
        return historyTask;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IllegalAccessException, IOException {
        FileBackedTasksManager fileBackedTasksManager = null;
        if (file.length() != 0) {
            ArrayList<String> fileBacked = reader(file);
            String fileBackedHistory = "";
            if (fileBacked.get(fileBacked.size() - 2).isEmpty()) {
                fileBackedHistory = fileBacked.get(fileBacked.size() - 1);
            }
            String fileNew = "resources/newFileBackedTaskManager.csv";
            fileBackedTasksManager = new FileBackedTasksManager(fileNew);
            fileBacked.remove(0);
            if (!Objects.equals(fileBackedHistory, "")) {
                fileBacked.remove(fileBacked.size() - 1);
                fileBacked.remove(fileBacked.size() - 1);
            }
            for (String taskLine : fileBacked) {
                String[] line = taskLine.split(",");
                NameTask type = NameTask.valueOf(line[1]);
                switch (type) {
                    case TASK: {
                        int taskId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getTaskList().put(taskId,
                               fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case EPIC: {
                        int epicId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getEpicList().put(epicId,
                                (Epic) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    case SUBTASK: {
                        int subtaskId = Integer.parseInt(line[0]);
                        fileBackedTasksManager.getSubtaskList().put(subtaskId,
                                (Subtask) fileBackedTasksManager.fromString(taskLine));
                        break;
                    }
                    default:
                        System.out.println("неверный тип задачи: " + type);
                        throw new IllegalAccessException();
                }
            }
            for (Integer taskId : historyFromString(fileBackedHistory)) {
                fileBackedTasksManager.findTaskId(taskId);
            }
            fileBackedTasksManager.save();
        }
        return fileBackedTasksManager;
    }

    public static ArrayList<String> reader(File file) {
        ArrayList<String> fileBacked = new ArrayList<>();
        if (file.length() != 0) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    fileBacked.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileBacked;
    }

    public String toString(Task task) {
        StringJoiner taskString = new StringJoiner(",").add(String.valueOf(task.getId())).
                add(String.valueOf(TASK)).add(task.getNameTask()).add(String.valueOf(task.getStatus())).
                add(task.getDescription());
        return taskString.toString();
    }

    public String toString(Epic epic) {
        StringJoiner epicString = new StringJoiner(",").add(String.valueOf(epic.getId())).
                add(String.valueOf(EPIC)).add(epic.getNameTask()).add(String.valueOf(epic.getStatus())).
                add(epic.getDescription());
        return epicString.toString();
    }

    public String toString(Subtask subtask) {
        StringJoiner subtaskString = new StringJoiner(",").add(String.valueOf(subtask.getId())).
                add(String.valueOf(SUBTASK)).add(subtask.getNameTask()).add(String.valueOf(subtask.getStatus())).
                add(subtask.getDescription()).add(String.valueOf(subtask.getIdEpic()));
        return subtaskString.toString();
    }

    @Override
    public Task addTask(Task task) throws IOException {
        super.addTask(task);
        save();
        return getTaskList().get(task.getId());
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.addEpic(epic, subtasks);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.updateEpic(epic, subtasks);
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
        //fileBackedTasksManager.findTaskId(3);
        fileBackedTasksManager.findTaskId(2);
        fileBackedTasksManager.findTaskId(1);

        fileBackedTasksManager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), DONE,
                task1.getId()));

        fileBackedTasksManager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), DONE,
                        epic2.getId(), epic2.getIdSubtaskEpic())),
                new ArrayList<>(Collections.singleton
                        (new Subtask(fileBackedTasksManager.getSubtaskList().get(6).getNameTask(),
                                fileBackedTasksManager.getSubtaskList().get(6).getDescription(), IN_PROGRESS,
                                fileBackedTasksManager.getSubtaskList().get(6).getId(),
                                fileBackedTasksManager.getSubtaskList().get(6).getIdEpic()))));

        System.out.println(fileBackedTasksManager.historyManager.getHistory());
        //System.out.println(fileBackedTasksManager.historyToStringList);
        //fileBackedTasksManager.printAllTask();
        //fileBackedTasksManager.findTaskId(3);
        //fileBackedTasksManager.findTaskId(1);
        //fileBackedTasksManager.findTaskId(4);
        //fileBackedTasksManager.printAllTask();
        //System.out.println(fileBackedTasksManager.historyToStringList);*/
    }
}


