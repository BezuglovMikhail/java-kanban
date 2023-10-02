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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.Month.FEBRUARY;
import static ru.yandex.practicum.project.status.Status.*;
import static ru.yandex.practicum.project.task.NameTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String fileName;

    public FileBackedTasksManager() {
        fileName = null;
    }

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName), StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,type,name,status,description,startTime,duration,epic");
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
            throw new ManagerSaveException("Ошибка ввода вывода", e);
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
        LocalDateTime startTime = LocalDateTime.parse(task[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(task[6]));
        switch (type) {
            case TASK: {
                prioritizedTasks.add(new Task(name, description, status, id, startTime, duration));
                return new Task(name, description, status, id, startTime, duration);
            }
            case EPIC: {
                return new Epic(name, description, status, id, startTime, duration);
            }
            case SUBTASK: {
                int epic = Integer.parseInt(task[7]);
                prioritizedTasks.add(new Subtask(name, description, status, id, startTime, duration, epic));
                return new Subtask(name, description, status, id, startTime, duration, epic);
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

    public static FileBackedTasksManager loadFileBacked(String fileName) throws IllegalAccessException, IOException, InterruptedException {
        FileBackedTasksManager fileBackedTasksManager = null;
        File file = new File(fileName);
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
                add(task.getDescription()).add(task.getStartTime().toString()).
                add(String.valueOf(task.getDuration().toMinutes()));
        return taskString.toString();
    }

    public String toString(Epic epic) {
        StringJoiner epicString = new StringJoiner(",").add(String.valueOf(epic.getId())).
                add(String.valueOf(EPIC)).add(epic.getNameTask()).add(String.valueOf(epic.getStatus())).
                add(epic.getDescription()).add(epic.getStartTime().toString()).
                add(String.valueOf(epic.getDuration().toMinutes()));
        return epicString.toString();
    }

    public String toString(Subtask subtask) {
        StringJoiner subtaskString = new StringJoiner(",").add(String.valueOf(subtask.getId())).
                add(String.valueOf(SUBTASK)).add(subtask.getNameTask()).add(String.valueOf(subtask.getStatus())).
                add(subtask.getDescription()).add(subtask.getStartTime().toString()).
                add(String.valueOf(subtask.getDuration().toMinutes())).add(String.valueOf(subtask.getIdEpic()));
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
        return getEpicList().get(epic.getId());
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
        return getTaskList().get(task.getId());
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        super.updateEpic(epic, subtasks);
        save();
        return getEpicList().get(epic.getId());
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        Task taskFound = super.findTaskId(taskId);
        historyToString(historyManager);
        save();
        return taskFound;
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        ArrayList<Subtask> subtasksForEpicId = super.findSubtaskForEpicId(epicId);
        historyToString(historyManager);
        save();
        return subtasksForEpicId;
    }

    @Override
    public void printAllTask() throws IOException {
        if (getEpicList().size() != 0 || getSubtaskList().size() != 0 || getTaskList().size() != 0) {
            super.printAllTask();
            historyToString(historyManager);
            save();
        }
    }

    @Override
    public void cleanTask() throws IOException {
        super.cleanTask();
    }

    @Override
    public void findTaskIdAndRemove(int taskId) throws IOException {
        super.findTaskIdAndRemove(taskId);
        historyToString(historyManager);
        save();
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {
        FileBackedTasksManager fileBackedTasksManager =
                new FileBackedTasksManager("resources/taskAndHistoryTask.csv");
        loadFileBacked("resources/taskAndHistoryTask.csv");

        Task task1 = new Task("Прогулка", "Одеться и пойти гулять",
                LocalDateTime.of(2023, FEBRUARY, 13, 19, 30), Duration.ofMinutes(15));

        Epic epic1 = new Epic("Переезд", "Переезд в другой город");
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Составить список", "Список вещей для переезда",
                LocalDateTime.of(2023, FEBRUARY, 13, 20, 30), Duration.ofMinutes(15)));
        subtasks1.add(new Subtask("Упаковать вещи",
                "Компактно упаковать вещи в коробки и промаркировать",
                LocalDateTime.of(2023, FEBRUARY, 13, 21, 0), Duration.ofMinutes(15)));

        Epic epic2 = new Epic("Прогулка с детьми", "Собрать детей взять снегокат и пойти гулять");
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Одеть детей", "Поймать и одеть детей",
                LocalDateTime.of(2023, FEBRUARY, 13, 21, 40), Duration.ofMinutes(15)));

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.printAllTask();
        fileBackedTasksManager.addEpic(epic1, subtasks1);
        fileBackedTasksManager.addEpic(epic2, subtasks2);
        fileBackedTasksManager.findTaskId(5);
        fileBackedTasksManager.findTaskId(3);
        fileBackedTasksManager.findTaskId(2);
        fileBackedTasksManager.findTaskId(1);
        System.out.println(fileBackedTasksManager.getPrioritizedTasks());
        fileBackedTasksManager.findTaskId(3);
        fileBackedTasksManager.findTaskIdAndRemove(3);
        fileBackedTasksManager.findTaskId(2);
        fileBackedTasksManager.findTaskId(4);
        System.out.println(fileBackedTasksManager.getPrioritizedTasks());
        fileBackedTasksManager.updateTask(new Task(task1.getNameTask(), task1.getDescription(), DONE,
                task1.getId(), task1.getStartTime(), task1.getDuration()));

        fileBackedTasksManager.updateEpic((new Epic(epic2.getNameTask(), epic2.getDescription(), NEW,
                        epic2.getId(), epic2.getIdSubtaskEpic(),
                        fileBackedTasksManager.getEpicList().get(5).getStartTime(),
                        fileBackedTasksManager.getEpicList().get(5).getDuration())),
                new ArrayList<>(Collections.singleton
                        (new Subtask(fileBackedTasksManager.getSubtaskList().get(6).getNameTask(),
                                fileBackedTasksManager.getSubtaskList().get(6).getDescription(), IN_PROGRESS,
                                fileBackedTasksManager.getSubtaskList().get(6).getId(),
                                fileBackedTasksManager.getSubtaskList().get(6).getStartTime(),
                                fileBackedTasksManager.getSubtaskList().get(6).getDuration(),
                                fileBackedTasksManager.getSubtaskList().get(6).getIdEpic()))));
    }
}
