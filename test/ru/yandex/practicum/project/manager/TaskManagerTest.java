package ru.yandex.practicum.project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task task_StatusNewTest;
    protected Task task_StatusInProgressTest;
    protected Task task_StatusDoneTest;

    protected Epic epic1_StatusNewTest;
    protected Epic epic1_StatusInProgressTest;

    protected Epic epic2_StatusNewTest;
    protected Epic epic2_StatusInProgressTest;
    protected Epic epic2_StatusDoneTest;

    protected Subtask subtaskEpic1_StatusNewTest;
    protected Subtask subtaskEpic1_StatusInProgressTest;

    protected Subtask subtaskEpic2_1_StatusNewTest;
    protected Subtask subtaskEpic2_1_StatusInProgressTest;
    protected Subtask subtaskEpic2_1_StatusDoneTest;
    protected Subtask subtaskEpic2_2_StatusNewTest;
    protected Subtask subtaskEpic2_2_StatusDoneTest;


    @BeforeEach
    void create() {
        task_StatusNewTest = new Task("Простая задача для теста", "Описание простой задачи для теста");
        task_StatusInProgressTest = new Task("Простая задача для теста", "Описание простой задачи для теста", Status.IN_PROGRESS, 1);
        epic1_StatusNewTest = new Epic("Эпик с одной подзадачей для теста",
                "Описание эпика с одной подзадачей для теста");
        epic1_StatusInProgressTest = new Epic("Эпик с одной подзадачей для теста",
                "Описание эпика с одной подзадачей для теста", Status.IN_PROGRESS, 1);
        subtaskEpic1_StatusNewTest = new Subtask("Имя подзадачи epic1 для теста",
                "Описание подзадачи epic1 для теста");
        subtaskEpic1_StatusInProgressTest = new Subtask("Имя подзадачи epic1 для теста",
                "Описание подзадачи epic1 для теста", Status.IN_PROGRESS, 2, 1);

        epic2_StatusNewTest = new Epic("Эпик с двумя подзадачами для теста",
                "Описание эпика с двумя подзадачами для теста");
        epic2_StatusInProgressTest = new Epic("Эпик с двумя подзадачами для теста",
                "Описание эпика с двумя подзадачами для теста", Status.IN_PROGRESS, 1);
        epic2_StatusDoneTest = new Epic("Эпик с двумя подзадачами для теста",
                "Описание эпика с двумя подзадачами для теста", Status.DONE, 1);
        subtaskEpic2_1_StatusNewTest = new Subtask("Имя первой подзадачи epic2 для теста",
                "Описание первой подзадачи epic2 для теста");
        subtaskEpic2_1_StatusInProgressTest = new Subtask("Имя первой подзадачи epic2 для теста",
                "Описание первой подзадачи epic2 для теста", Status.IN_PROGRESS, 2, 1);
        subtaskEpic2_1_StatusDoneTest = new Subtask("Имя первой подзадачи epic2 для теста",
                "Описание первой подзадачи epic2 для теста", Status.DONE, 2, 1);
        subtaskEpic2_2_StatusNewTest = new Subtask("Имя второй подзадачи epic2 для теста",
                "Описание второй подзадачи epic2 для теста");
        subtaskEpic2_2_StatusDoneTest = new Subtask("Имя второй подзадачи epic2 для теста",
                "Описание второй подзадачи epic2 для теста", Status.DONE, 3, 1);
    }

    @BeforeEach
    abstract void createTasks();

    @Test
    void addTask() {
    }

    @Test
    void addEpic() {
    }

    @Test
    void updateTask() throws IOException {
    }

    @Test
    void updateEpic() throws IOException {
    }

    @Test
    void findTaskId() throws IOException {
    }

    @Test
    void findTaskIdAndRemove() {
    }

    @Test
    void findSubtaskForEpicId() {
    }

    @Test
    void cleanTask() throws IOException {
    }

    @Test
    void printAllTask() {
    }
}