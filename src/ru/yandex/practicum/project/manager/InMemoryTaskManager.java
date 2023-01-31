package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import static ru.yandex.practicum.project.status.Status.DONE;
import static ru.yandex.practicum.project.status.Status.IN_PROGRESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();
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

    public void setTaskList(HashMap<Integer, Task> taskList) {
        this.taskList = taskList;
    }

    public void setEpicList(HashMap<Integer, Epic> epicList) {
        this.epicList = epicList;
    }

    public void setSubtaskList(HashMap<Integer, Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public Task addTask(Task task) throws IOException {
        id++;
        task.setStatus(String.valueOf(Status.NEW));
        task.setId(id);
        taskList.put(id, task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        id++;
        epic.setId(id);
        epic.setStatus(String.valueOf(Status.NEW));
        ArrayList<Integer> idSubtaskEpic = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            id++;
            subtask.setId(id);
            subtask.setStatus(String.valueOf(Status.NEW));
            subtask.setIdEpic(epic.getId());
            idSubtaskEpic.add(id);
            subtaskList.put(id, subtask);
        }
        epic.setIdSubtaskEpic(idSubtaskEpic);
        epicList.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        if (Objects.equals(task.getStatus(),
                String.valueOf(IN_PROGRESS)) || Objects.equals(task.getStatus(),
                String.valueOf(DONE))) {
            task.setStatus(task.getStatus());
            taskList.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        int numberDoneSubtask = 0;
        int numberInProgressSubtask = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals("DONE")) {
                numberDoneSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                numberInProgressSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            }
        }
        if (numberDoneSubtask == subtasks.size()) {
            epic.setStatus(String.valueOf(Status.DONE));
            epicList.put(epic.getId(), epic);
        } else if (numberInProgressSubtask >= 1) {
            epic.setStatus(String.valueOf(IN_PROGRESS));
            epicList.put(epic.getId(), epic);
        }
        return epic;
    }

    @Override
    public Task findTaskId(int taskId) throws IOException {
        if (epicList.containsKey(taskId)) {
            historyManager.add(epicList.get(taskId));
            return epicList.get(taskId);
        } else if (subtaskList.containsKey(taskId)) {
            historyManager.add(subtaskList.get(taskId));
            return subtaskList.get(taskId);
        } else if (taskList.containsKey(taskId)) {
            historyManager.add(taskList.get(taskId));
            return taskList.get(taskId);
        }
        return null;
    }

    @Override
    public void findTaskIdAndRemove(int taskId) {
        if (epicList.containsKey(taskId)) {
            for (int idSubtask : epicList.get(taskId).getIdSubtaskEpic()) {
                subtaskList.remove(idSubtask);
                historyManager.remove(id);
            }
            epicList.remove(taskId);
            historyManager.remove(id);
        } else if (subtaskList.containsKey(taskId)) {
            for (int i : epicList.keySet()) {
                Epic epic = epicList.get(i);
                for (int id : epic.getIdSubtaskEpic()) {
                    if (id == taskId) {
                        epicList.get(i).getIdSubtaskEpic().remove((Integer) taskId);
                        subtaskList.remove(taskId);
                        historyManager.remove(id);
                        break;
                    }
                }
            }
        } else if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
            historyManager.remove(id);
        } else {
            System.out.println("Неверный тип задачи или задача удалена");
        }
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) throws IOException {
        ArrayList<Subtask> subtasksEpicId = new ArrayList<>();
        for (int id : epicList.get(epicId).getIdSubtaskEpic()) {
            subtasksEpicId.add(subtaskList.get(id));
            historyManager.add(subtaskList.get(id));
        }
        return subtasksEpicId;
    }

    @Override
    public void printAllTask() throws IOException {
        System.out.println(getTaskList());
        for (int idTask : getTaskList().keySet()) {
            historyManager.add(getTaskList().get(idTask));
        }
        System.out.println(getEpicList());
        for (int idEpic : getEpicList().keySet()) {
            historyManager.add(getEpicList().get(idEpic));
        }
        System.out.println(getSubtaskList());
        for (int idSubTask : getSubtaskList().keySet()) {
            historyManager.add(getSubtaskList().get(idSubTask));
        }
    }

    @Override
    public void cleanTask() {
        epicList.clear();
        subtaskList.clear();
        taskList.clear();
        historyManager.clear();
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
