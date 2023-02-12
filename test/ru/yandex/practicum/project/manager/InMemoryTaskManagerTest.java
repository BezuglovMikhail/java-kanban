package ru.yandex.practicum.project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.project.status.Status;
import ru.yandex.practicum.project.task.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Override
    @BeforeEach
    void createTasks() {
        super.create();
        manager = new InMemoryTaskManager();
    }

    @Test
    void createTask() throws IOException {
        manager.addTask(task_StatusNewTest);
        assertEquals(Status.NEW, manager.getTaskList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
    }

    @Test
    void createNullTask() throws IOException {
        Task task1 = new Task(null,null);
        manager.addTask(task1);
        assertNull(manager.getTaskList().get(1), "Нельзя создать пустую задачу");
    }

    @Test
    void createNullTask1() throws IOException {
        Task task1 = new Task("","");
        manager.addTask(task1);

        assertNull(manager.getTaskList().get(1), "Нельзя создать пустую задачу");
        //assertEquals(1, manager.getTaskList().get(1).getId(), "Нельзя создать пустую задачу");
    }

    @Test
    void createTaskWithNotNewStatus() throws IOException {
        Task task2 = new Task("Задача со статусом IN_PROGRESS",
                "Описание задачи со статусом IN_PROGRESS", Status.IN_PROGRESS, 1);
        Task task3 = new Task("Задача со статусом DONE",
                "Описание задачи со статусом DONE", Status.DONE, 2);
        manager.addTask(task2);
        manager.addTask(task3);
        assertEquals(Status.NEW, manager.getTaskList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getTaskList().get(2).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
    }

    @Test
    void createEpicWithOneSubtask() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest,epicSubtasks);
        assertEquals(Status.NEW, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
    }

    @Test
    void createEpicWithOneSubtaskAndStatusInProgress() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic1_StatusInProgressTest));
        manager.addEpic(epic1_StatusInProgressTest, epicSubtasks);
        assertEquals(Status.NEW, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
    }

    @Test
    void createEpicWithTwoSubtask() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest,epicSubtasks);
        assertEquals(Status.NEW, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(3).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
        assertEquals(3, manager.getSubtaskList().get(3).getId(), "Неверный id подзадачи, ожидался: " + 3);
    }

    @Test
    void createEpicWithTwoSubtaskWithDoneStatus() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusDoneTest, subtaskEpic2_2_StatusDoneTest));
        manager.addEpic(epic2_StatusInProgressTest, epicSubtasks);
        assertEquals(Status.NEW, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(3).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
        assertEquals(3, manager.getSubtaskList().get(3).getId(), "Неверный id подзадачи, ожидался: " + 3);
    }

    @Test
    void createNullEpic() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        Epic epicNull = new Epic("","");
        manager.addEpic(epicNull, epicSubtasks);
        assertNull(manager.getEpicList().get(1), "Нельзя создать пустую задачу");
    }

    @Test
    void createEpicWithNullSubtask() throws IOException {
        manager.addEpic(epic1_StatusNewTest, null);
        assertNull(manager.getEpicList().get(1), "Нельзя создать пустую задачу");
    }

    @Test
    void updateTaskWithStatusInProgress() throws IOException {
        manager.updateTask(task_StatusInProgressTest);
        assertEquals(Status.IN_PROGRESS, manager.getTaskList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.IN_PROGRESS);
        assertEquals(1, manager.getTaskList().get(1).getId(), "Неверный id задачи, ожидался: " + 1);
    }

    @Test
    void updateTaskWithStatusNew() throws IOException {
        manager.addTask(task_StatusNewTest);
        manager.updateTask(task_StatusNewTest);
        assertEquals(Status.NEW, manager.getTaskList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(1, manager.getTaskList().get(1).getId(), "Неверный id задачи, ожидался: " + 1);
    }

    @Test
    void updateEpicWithStatusSubtaskInProgress() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks);
        epicSubtasks.set(0, subtaskEpic1_StatusInProgressTest);
        manager.updateEpic(epic1_StatusInProgressTest, epicSubtasks);
        assertEquals(Status.IN_PROGRESS, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.IN_PROGRESS);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
    }

    @Test
    void updateEpicWithStatusSubtaskNewAndDone() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks);
        epicSubtasks.set(1, subtaskEpic2_2_StatusDoneTest);
        manager.updateEpic(epic1_StatusNewTest, epicSubtasks);
        assertEquals(Status.IN_PROGRESS, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.IN_PROGRESS);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(Status.DONE, manager.getSubtaskList().get(3).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.DONE);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
        assertEquals(3, manager.getSubtaskList().get(3).getId(), "Неверный id подзадачи, ожидался: " + 3);
    }

    @Test
    void updateEpicWithStatusSubtaskDoneAndDone() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        epicSubtasks.set(0, subtaskEpic2_1_StatusDoneTest);
        epicSubtasks.set(1, subtaskEpic2_2_StatusDoneTest);
        manager.updateEpic(epic2_StatusNewTest, epicSubtasks);
        assertEquals(Status.DONE, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.DONE);
        assertEquals(Status.DONE, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.DONE);
        assertEquals(Status.DONE, manager.getSubtaskList().get(3).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.DONE);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
        assertEquals(3, manager.getSubtaskList().get(3).getId(), "Неверный id подзадачи, ожидался: " + 3);
    }

    @Test
    void updateEpicStatusDoneWithStatusSubtaskNewAndNew() throws IOException {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        manager.updateEpic(epic2_StatusDoneTest, epicSubtasks);
        assertEquals(Status.NEW, manager.getEpicList().get(1).getStatus(), "Неверный статус задачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(2).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(Status.NEW, manager.getSubtaskList().get(3).getStatus(), "Неверный статус подзадачи, ожидался: " + Status.NEW);
        assertEquals(1, manager.getEpicList().get(1).getId(), "Неверный id эпика, ожидался: " + 1);
        assertEquals(2, manager.getSubtaskList().get(2).getId(), "Неверный id подзадачи, ожидался: " + 2);
        assertEquals(3, manager.getSubtaskList().get(3).getId(), "Неверный id подзадачи, ожидался: " + 3);
    }

    @Test
    void findTask0Id() throws IOException {
        manager.findTaskId(0);
        assertNull(manager.getTaskList().get(0));
        assertNull(manager.getEpicList().get(0));
        assertNull(manager.getSubtaskList().get(0));
    }

    @Test
    void findTaskId1() throws IOException {
        manager.addTask(task_StatusNewTest);
        manager.findTaskId(1);
        assertEquals(task_StatusNewTest, manager.getTaskList().get(1));
        assertNull(manager.getEpicList().get(1));
        assertNull(manager.getSubtaskList().get(1));
    }

    @Test
    void findTaskId3() throws IOException {
        manager.addTask(task_StatusNewTest);
        manager.findTaskId(1000);
        assertNull(manager.getTaskList().get(1000));
        assertNull(manager.getEpicList().get(1000));
        assertNull(manager.getSubtaskList().get(1000));
    }

    @Test
    void findTaskId2() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        manager.findTaskId(3);
        assertNull(manager.getTaskList().get(3));
        assertNull(manager.getEpicList().get(3));
        assertEquals(subtaskEpic2_1_StatusNewTest, manager.getSubtaskList().get(3));
    }

    @Test
    void findTaskIdAndRemove0() throws IOException {
        manager.findTaskIdAndRemove(0);
        assertNull(manager.getTaskList().get(0));
        assertNull(manager.getEpicList().get(0));
        assertNull(manager.getSubtaskList().get(0));
    }

    @Test
    void findTaskIdAndRemove1() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        manager.findTaskIdAndRemove(1);
        manager.findTaskIdAndRemove(3);
        manager.findTaskIdAndRemove(2);
        manager.findTaskIdAndRemove(4);
        assertNull(manager.getTaskList().get(1));
        assertNull(manager.getSubtaskList().get(3));
        assertNull(manager.getEpicList().get(2));
        assertNull(manager.getSubtaskList().get(4));
    }

    @Test
    void findTaskIdAndRemove2() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        manager.findTaskIdAndRemove(1000);
        assertNull(manager.getTaskList().get(1000));
        assertNull(manager.getEpicList().get(1000));
        assertNull(manager.getSubtaskList().get(1000));
    }

    @Test
    void findSubtaskForEpicId0() throws IOException {
        manager.findSubtaskForEpicId(1);
        assertNull(manager.getEpicList().get(1));
    }

    @Test
    void findSubtaskForEpicId1() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.findSubtaskForEpicId(2);
        assertEquals(2,manager.getEpicList().get(2).getId());
        assertEquals(List.of(3), manager.getEpicList().get(2).getIdSubtaskEpic());
    }

    @Test
    void findSubtaskForEpicId2() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.findSubtaskForEpicId(4);
        assertEquals(4,manager.getEpicList().get(4).getId());
        assertEquals(List.of(5, 6), manager.getEpicList().get(4).getIdSubtaskEpic());
    }

    @Test
    void findSubtaskForEpicId1000() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks);
        manager.findSubtaskForEpicId(1000);
        assertNull(manager.getEpicList().get(1000));
    }

    @Test
    void cleanTask() throws IOException {
        manager.addTask(task_StatusNewTest);
        ArrayList<Subtask> epicSubtasks1 = new ArrayList<>(List.of(subtaskEpic1_StatusNewTest));
        manager.addEpic(epic1_StatusNewTest, epicSubtasks1);
        ArrayList<Subtask> epicSubtasks2 = new ArrayList<>(List.of(subtaskEpic2_1_StatusNewTest, subtaskEpic2_2_StatusNewTest));
        manager.addEpic(epic2_StatusNewTest, epicSubtasks2);
        manager.cleanTask();
        assertTrue(manager.getEpicList().isEmpty(), "Список эпиков после удаления должен быть пустой!");
        assertTrue(manager.getTaskList().isEmpty(), "Список задач после удаления должен быть пустой!");
        assertTrue(manager.getSubtaskList().isEmpty(), "Список подзадач после удаления должен быть пустой!");
    }
}