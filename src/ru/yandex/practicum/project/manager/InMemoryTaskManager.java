package ru.yandex.practicum.project.manager;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;
import static ru.yandex.practicum.project.status.Status.DONE;
import static ru.yandex.practicum.project.status.Status.IN_PROGRESS;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private int id = 0;

    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    @Override
    public Task addTask(Task task) {
        id++;
        task.setStatus(String.valueOf(Status.NEW));
        task.setId(id);
        taskList.put(id, task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) {
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
    public Task updateTask(Task task) {
        if (Objects.equals(task.getStatus(), String.valueOf(IN_PROGRESS))
                || Objects.equals(task.getStatus(), String.valueOf(DONE))) {
            task.setStatus(task.getStatus());
            taskList.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) {
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
    public void findTaskId(int taskId) {
        if (!(epicList.get(taskId) == null)) {
            System.out.println(epicList.get(taskId));
            historyManager.add(epicList.get(taskId));

        } else if (!(subtaskList.get(taskId) == null)) {
            System.out.println(subtaskList.get(taskId));
            historyManager.add(subtaskList.get(taskId));

        } else if (!(taskList.get(taskId) == null)) {
            System.out.println(taskList.get(taskId));
            //historyManager.historyList.add(taskId);
            historyManager.add(taskList.get(taskId));

        } else {
            System.out.println("Такой задачи нет или задача удалена");
        }
    }

    @Override
    public void findTaskIdAndRemove(int taskId) {
        if (!(epicList.get(taskId) == null)) {

            for (int idSubtask : epicList.get(taskId).getIdSubtaskEpic()) {
                subtaskList.remove(idSubtask);
            }
            epicList.remove(taskId);

        } else if (!(subtaskList.get(taskId) == null)) {

            for (int i : epicList.keySet()) {
                Epic epic = epicList.get(i);

                for (int id : epic.getIdSubtaskEpic()) {
                    if (id == taskId) {
                        epicList.get(i).getIdSubtaskEpic().remove((Integer) taskId);
                        subtaskList.remove(taskId);
                        break;
                    }
                }
            }
        } else if (!(taskList.get(taskId) == null)) {
            taskList.remove(taskId);
        } else {
            System.out.println("Неверный тип задачи или задача удалена");
        }
    }

    @Override
    public ArrayList<Subtask> findSubtaskForEpicId(int epicId) {
        ArrayList<Subtask> subtasksEpicId = new ArrayList<>();

            for (int id : epicList.get(epicId).getIdSubtaskEpic()) {
                subtasksEpicId.add(subtaskList.get(id));
                historyManager.add(subtaskList.get(id));

        }
        return subtasksEpicId;
    }

    @Override
    public void printAllTask() {
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
    public InMemoryHistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void cleanTask() {
        epicList.clear();
        subtaskList.clear();
        taskList.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager manager = (InMemoryTaskManager) o;
        return id == manager.id && Objects.equals(taskList, manager.taskList)
                && Objects.equals(epicList, manager.epicList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskList, epicList, id);
    }

    @Override
    public String toString() {
        return "Manager{" + "\n\t" +
                "taskList=" + "\n\t" + taskList.entrySet() + "\n\t" +
                "epicList=" + "\n\t" + epicList + '}' + "\n\t";
    }
}
