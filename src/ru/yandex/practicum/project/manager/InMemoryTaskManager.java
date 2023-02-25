package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.*;

import static ru.yandex.practicum.project.status.Status.*;
import static ru.yandex.practicum.project.task.NameTask.EPIC;
import static ru.yandex.practicum.project.task.NameTask.TASK;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private int id = 0;

    public HistoryManager<Task> historyManager = Managers.getDefaultHistory();

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    public TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    @Override
    public Task addTask(Task task) throws IOException {
        if (task.getNameTask() != null
                && task.getDescription() != null
                && !task.getDescription().equals("")
                && !task.getNameTask().equals("")
                && checkCrossTask(task, getPrioritizedTasks())) { // проверка на пересечение задач при добавлении
            id++;
            task.setStatus(NEW);
            task.setId(id);
            task.setStartTime(task.getStartTime());
            task.setDuration(task.getDuration());
            task.setType(TASK);
            task.setEndTime(task.getStartTime(), task.getDuration());
            prioritizedTasks.add(task);
            taskList.put(id, task);
            return task;
        } else if (task.getNameTask() == null || Objects.equals(task.getNameTask(), "")) {
            System.out.println("Нельзя добавить пустую задачу");
            return null;
        } else {
            throw new IllegalArgumentException("Задачи пересекаются по времени!");
        }
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        if (epic.getNameTask() != null
                && epic.getDescription() != null
                && !Objects.equals(epic.getNameTask(), "")
                && !Objects.equals(epic.getDescription(), "")
                && subtasks != null) {
            id++;
            epic.setId(id);
            epic.setStatus(NEW);
            epic.setType(EPIC);
            ArrayList<Integer> idSubtaskEpic = new ArrayList<>();

            for (Subtask subtask : subtasks) {
                if (checkCrossTask(subtask, getPrioritizedTasks())) { // проверка на пересечение задач при добавлении
                    id++;
                    subtask.setId(id);
                    subtask.setStatus((NEW));
                    subtask.setIdEpic(epic.getId());
                    idSubtaskEpic.add(id);
                    subtask.setStartTime(subtask.getStartTime());
                    subtask.setDuration(subtask.getDuration());
                    prioritizedTasks.add(subtask);
                    subtaskList.put(id, subtask);
                } else if (epic.getNameTask() == null || Objects.equals(epic.getNameTask(), "")) {
                    throw new IllegalArgumentException("Пустую задачу нельзя создать");
                } else {
                    throw new IllegalArgumentException("Подзадачи пересекаются по времени!");
                }

            }
            epic.setIdSubtaskEpic(idSubtaskEpic);
            epic.setStartTime(calculationStartTimeEpic(idSubtaskEpic));
            epic.setDuration(calculationDuration(idSubtaskEpic, epic.getStartTime()));
            epic.setEndTime(epic.getStartTime(), epic.getDuration());
            epicList.put(epic.getId(), epic);
            return epic;
        } else {
            System.out.println("Эпик или подзадачи эпика не могут быть пустыми");
        }
        return null;
    }

    public LocalDateTime calculationStartTimeEpic(ArrayList<Integer> idSubtaskEpic) {
        LocalDateTime startTimeEpic = LocalDateTime.MAX;

        for (int idSubtask : idSubtaskEpic) {
            LocalDateTime min = getSubtaskList().get(idSubtask).getStartTime();
            if (min != null) {
                if (min.isBefore(startTimeEpic)) {
                    startTimeEpic = min;
                }
            }
        }
        return startTimeEpic;
    }

    public Duration calculationDuration(ArrayList<Integer> idSubtaskEpic, LocalDateTime startTimeEpic) {
        LocalDateTime endTimeEpic = startTimeEpic;
        Duration duration = null;
        for (int idSubtask : idSubtaskEpic) {
            LocalDateTime max = getSubtaskList().get(idSubtask).getStartTime();
            if (max != null) {
                if (max.isAfter(endTimeEpic)) {
                    endTimeEpic = max;
                }
                if (startTimeEpic != endTimeEpic) {
                    duration = Duration.between(startTimeEpic, endTimeEpic.plusMinutes(15));
                } else {
                    duration = Duration.ofMinutes(15);
                }
            }
        }
        return duration;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        prioritizedTasks.remove(taskList.get(task.getId()));
        if (checkCrossTask(task, getPrioritizedTasks())) {  // проверка на пересечение задач при обновлении
            if (Objects.equals(task.getStatus(), IN_PROGRESS) || Objects.equals(task.getStatus(), DONE)) {
                task.setStatus(task.getStatus());
                prioritizedTasks.add(task);
                taskList.put(task.getId(), task);
            }
            return task;
        } else {
            throw new IllegalArgumentException("Подзадачи пересекаются по времени!");
        }
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        int numberDoneSubtask = 0;
        int numberInProgressSubtask = 0;
        int numberNewSubtask = 0;
        ArrayList<Integer> idSubtaskEpic = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            idSubtaskEpic.add(subtask.getId());
        }
        epic.setStartTime(calculationStartTimeEpic(idSubtaskEpic));
        epic.setDuration(calculationDuration(idSubtaskEpic, epic.getStartTime()));

        for (Subtask subtask : subtasks) {
            prioritizedTasks.remove(subtaskList.get(subtask.getId()));
            if (checkCrossTask(subtask, getPrioritizedTasks())) {  // проверка на пересечение задач при обновлении
                if (subtask.getStatus().equals(DONE)) {
                    numberDoneSubtask += 1;
                    prioritizedTasks.add(subtask);
                    subtaskList.put(subtask.getId(), subtask);
                } else if (subtask.getStatus().equals(IN_PROGRESS)) {
                    numberInProgressSubtask += 1;
                    prioritizedTasks.add(subtask);
                    subtaskList.put(subtask.getId(), subtask);
                } else if (subtask.getStatus().equals(NEW)) {
                    numberNewSubtask += 1;
                    prioritizedTasks.add(subtask);
                    subtaskList.put(subtask.getId(), subtask);
                }
            } else {
                throw new IllegalArgumentException("Подзадачи пересекаются по времени!");
            }
        }
        if (numberDoneSubtask == subtasks.size()) {
            epic.setStatus(DONE);
            epicList.put(epic.getId(), epic);
        } else if (numberInProgressSubtask >= 1 || (numberDoneSubtask < subtasks.size() && numberDoneSubtask > 0)) {
            epic.setStatus(IN_PROGRESS);
            epicList.put(epic.getId(), epic);
        } else if (numberNewSubtask == subtasks.size()) {
            epic.setStatus(NEW);
            epicList.put(epic.getId(), epic);
        }
        return epicList.get(epic.getId());
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        Task taskFound = null;
        if (epicList.containsKey(taskId)) {
            historyManager.add(epicList.get(taskId));
            taskFound = epicList.get(taskId);
        } else if (subtaskList.containsKey(taskId)) {
            historyManager.add(subtaskList.get(taskId));
            taskFound = subtaskList.get(taskId);
        } else if (taskList.containsKey(taskId)) {
            historyManager.add(taskList.get(taskId));
            taskFound = taskList.get(taskId);
        }
        return taskFound;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean checkCrossTask(Task newTask, TreeSet<Task> prioritizedTasks) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        }
        if (newTask.getEndTime() == null) {
            newTask.setEndTime(newTask.getStartTime(), newTask.getDuration());
        }
        for (Task task : prioritizedTasks) {
            if (newTask.getStartTime().isBefore(task.getStartTime())
                    || newTask.getEndTime().isBefore(task.getStartTime())
                    || newTask.getEndTime().isBefore(task.getEndTime())
                    || newTask.getStartTime().isBefore(task.getEndTime())) {
                if (newTask.getStartTime().isAfter(task.getStartTime())
                        || newTask.getEndTime().isAfter(task.getStartTime())
                        || newTask.getEndTime().isAfter(task.getEndTime())
                        || newTask.getStartTime().isAfter(task.getEndTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void findTaskIdAndRemove(int taskId) throws IOException {
        if (epicList.containsKey(taskId)) {
            for (int idSubtask : epicList.get(taskId).getIdSubtaskEpic()) {
                prioritizedTasks.remove(subtaskList.get(idSubtask));
                subtaskList.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
            epicList.remove(taskId);
            historyManager.remove(taskId);
        } else if (subtaskList.containsKey(taskId)) {
            for (int i : epicList.keySet()) {
                Epic epic = epicList.get(i);
                for (int id : epic.getIdSubtaskEpic()) {
                    if (id == taskId) {
                        epicList.get(i).getIdSubtaskEpic().remove((Integer) taskId);
                        prioritizedTasks.remove(subtaskList.get(taskId));
                        subtaskList.remove(taskId);
                        historyManager.remove(taskId);
                        break;
                    }
                }
            }
        } else if (taskList.containsKey(taskId)) {
            prioritizedTasks.remove(taskList.get(taskId));
            taskList.remove(taskId);
            historyManager.remove(taskId);
        } else {
            System.out.println("Неверный id задачи или задача удалена");
        }
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        ArrayList<Subtask> subtasksEpicId = new ArrayList<>();
        if (epicList.containsKey(epicId)) {
            for (int id : epicList.get(epicId).getIdSubtaskEpic()) {
                subtasksEpicId.add(subtaskList.get(id));
                historyManager.add(subtaskList.get(id));
            }
            return subtasksEpicId;
        } else {
            System.out.println("Эпика с таким id нет");
        }
        return subtasksEpicId;
    }

    @Override
    public void printAllTask() throws IOException {
        if (getTaskList().size() != 0) {
            for (Task task : getTaskList().values()) {
                historyManager.add(task);
            }
            System.out.println(getTaskList());
        }
        if (getEpicList().size() != 0) {
            for (Epic epic : getEpicList().values()) {
                historyManager.add(epic);
            }
            System.out.println(getEpicList());
        }
        if (getSubtaskList().size() != 0) {
            for (Subtask subtask : getSubtaskList().values()) {
                historyManager.add(subtask);
            }
            System.out.println(getSubtaskList());
        } else {
            System.out.println("Список задач пуст.");
        }
    }

    @Override
    public void cleanTask() throws IOException {
        epicList.clear();
        subtaskList.clear();
        taskList.clear();
        historyManager.clear();
        prioritizedTasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager manager = (InMemoryTaskManager) o;
        return id == manager.id
                && Objects.equals(taskList, manager.taskList)
                && Objects.equals(epicList, manager.epicList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskList, epicList, id);
    }

    @Override
    public String toString() {
        return "Manager{" + "taskList=" + taskList.entrySet() + "epicList=" + epicList + '}';
    }
}
