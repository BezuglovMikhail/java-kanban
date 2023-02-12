package ru.yandex.practicum.project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.project.task.Subtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager>{


    @Override
    @BeforeEach
    void createTasks() {
        super.create();
        manager = new InMemoryTaskManager();
    }

    @Test
    void linkLast() {
    }

    @Test
    void removeNode() {
    }

    @Test
    void clear() {
    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }

    @Test
    void getHistory0() {
        assertTrue(manager.historyManager.getHistory().isEmpty(), "История вызова задач должна быть пустой!");
    }

    @Test
    void getHistory1() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.findTaskId(1);
        manager.findSubtaskForEpicId(4);
        manager.findTaskId(2);
        manager.findTaskId(3);
        assertEquals(List.of(manager.getTaskList().get(1), manager.getSubtaskList().get(5),
                manager.getSubtaskList().get(6), manager.getEpicList().get(2),
                manager.getSubtaskList().get(3)), manager.historyManager.getHistory());
    }

    @Test
    void getHistory2() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.findTaskId(1);
        manager.findSubtaskForEpicId(4);
        manager.findTaskId(2);
        manager.findTaskId(3);
        manager.findTaskId(2);
        manager.findTaskId(1);
        manager.findTaskId(3);
        assertEquals(List.of(manager.getSubtaskList().get(5),
                manager.getSubtaskList().get(6), manager.getEpicList().get(2), manager.getTaskList().get(1),
                        manager.getSubtaskList().get(3)), manager.historyManager.getHistory());
    }
    @Test
    void getHistory() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.findTaskId(1);
        manager.findSubtaskForEpicId(4);
        manager.findTaskId(2);
        manager.findTaskId(3);
        //manager.findTaskId(2);
        manager.findTaskIdAndRemove(1);
        //manager.findTaskId(3);
        assertEquals(List.of(manager.getSubtaskList().get(5), manager.getSubtaskList().get(6),
                manager.getEpicList().get(2), manager.getSubtaskList().get(3)),
                manager.historyManager.getHistory());
    }

}