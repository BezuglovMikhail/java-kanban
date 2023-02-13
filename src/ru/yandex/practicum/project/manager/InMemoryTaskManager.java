package ru.yandex.practicum.project.manager;

import ru.yandex.practicum.project.task.*;

import static ru.yandex.practicum.project.status.Status.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

    @Override
    public Task addTask(Task task) throws IOException {
            if (task.getNameTask() != null
                    && task.getDescription() != null
                    && !task.getDescription().equals("")
                    && !task.getNameTask().equals("")) {
                id++;
                task.setStatus(NEW);
                task.setId(id);
                taskList.put(id, task);
                return task;
           } else {
                System.out.println("Нельзя добавить пустую задачу");
            }
                return null;
    }

    @Override
    public Epic addEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
         if (epic.getNameTask() != null
                 && epic.getDescription() != null
                 && !Objects.equals(epic.getNameTask(), "")
                 && !Objects.equals(epic.getDescription(), "")
                 && subtasks != null) { //отлавливаем null длч теста на пустой список
            id++;
            epic.setId(id);
            epic.setStatus(NEW);
            ArrayList<Integer> idSubtaskEpic = new ArrayList<>();

            for (Subtask subtask : subtasks) {
                id++;
                subtask.setId(id);
                subtask.setStatus((NEW));
                subtask.setIdEpic(epic.getId());
                idSubtaskEpic.add(id);
                subtaskList.put(id, subtask);
            }
            epic.setIdSubtaskEpic(idSubtaskEpic);
            epicList.put(epic.getId(), epic);
             return epic;
        } else {
             System.out.println("Эпик или подзадачи эпика не могут быть пустыми");
         }
        return null;
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        if (Objects.equals(task.getStatus(),IN_PROGRESS) || Objects.equals(task.getStatus(), DONE)) {
            task.setStatus(task.getStatus());
            taskList.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic, ArrayList<Subtask> subtasks) throws IOException {
        int numberDoneSubtask = 0;
        int numberInProgressSubtask = 0;
        int numberNewSubtask = 0;
        for (Subtask subtask : subtasks)
            if (subtask.getStatus().equals(DONE)) {
                numberDoneSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            } else if (subtask.getStatus().equals(IN_PROGRESS)) {
                numberInProgressSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
            } else if (subtask.getStatus().equals(NEW)) {
                numberNewSubtask += 1;
                subtaskList.put(subtask.getId(), subtask);
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
    public void findTaskIdAndRemove(int taskId) throws IOException {
        if (epicList.containsKey(taskId)) {
            for (int idSubtask : epicList.get(taskId).getIdSubtaskEpic()) {
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
                        subtaskList.remove(taskId);
                        historyManager.remove(taskId);
                        break;
                    }
                }
            }
        } else if (taskList.containsKey(taskId)) {
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
        return null;
    }

    @Override
    public void printAllTask() throws IOException {
        if (getTaskList().size() != 0 ) {
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


        /*for (int idTask : getTaskList().keySet()) {
            historyManager.add(getTaskList().get(idTask));
        }
        System.out.println(getEpicList());
        for (int idEpic : getEpicList().keySet()) {
            historyManager.add(getEpicList().get(idEpic));
        }
        System.out.println(getSubtaskList());
        for (int idSubTask : getSubtaskList().keySet()) {
            historyManager.add(getSubtaskList().get(idSubTask));
        }*/


    }

    @Override
    public void cleanTask() throws IOException {
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
