package ru.yandex.practicum.project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.project.task.Subtask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    @BeforeEach
    void createTasks() throws IOException {
        super.create();
        String fileNameTest = "resources/test.csv";
        String fileNameLoadTest = "resources/newFileBackedTaskManager.csv";
        new FileOutputStream(fileNameTest, false).close();
        new FileOutputStream(fileNameLoadTest, false).close();

        manager = new FileBackedTasksManager(fileNameTest);
    }


    @Test
    void loadFromFileReadeEmptyFileTest() throws IOException, IllegalAccessException {
        FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        ArrayList<String> testTaskStringsLoad = FileBackedTasksManager.
                reader(new File("resources/newFileBackedTaskManager.csv"));

        assertTrue(testTaskStringsLoad.isEmpty(), "Файл не пустой");
    }

    @Test
    void saveAndLoadOneTaskTest() throws IOException, IllegalAccessException {
        manager.addTask(task_StatusNewTest);
        ArrayList<String> testTaskStrings = new ArrayList<>(List.of(
                "id,type,name,status,description,startTime,duration,epic",
                "1,TASK,Простая задача для теста,NEW,Описание простой задачи для теста,2023-02-13T19:30,15"));
        ArrayList<String> testTaskStringsSave = FileBackedTasksManager.reader(new File("resources/test.csv"));
        FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        ArrayList<String> testTaskStringsLoad = FileBackedTasksManager.
                reader(new File("resources/newFileBackedTaskManager.csv"));

        assertEquals(testTaskStrings, testTaskStringsSave);
        assertEquals(testTaskStringsSave, testTaskStringsLoad);
    }

    @Test
    void saveAndLoadOneTaskTwoEpicWithHistoryManagerTest() throws IOException, IllegalAccessException {
        manager.addTask(task_StatusNewTest);
        manager.addEpic(epic1_StatusNewTest, new ArrayList<Subtask>(List.of(subtaskEpic1_StatusInProgressTest)));
        manager.addEpic(epic2_StatusNewTest, new ArrayList<Subtask>(List.of(subtaskEpic2_1_StatusNewTest,
                subtaskEpic2_2_StatusNewTest)));
        manager.findSubtaskForEpicId(4);
        manager.findTaskId(1);
        manager.findTaskId(2);
        manager.findTaskId(3);
        //manager.findTaskIdAndRemove(2);
        manager.findTaskIdAndRemove(1);
        manager.findTaskIdAndRemove(6);

        ArrayList<String> testTaskStringsSave = FileBackedTasksManager.reader(new File("resources/test.csv"));
        FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        ArrayList<String> testTaskStringsLoad = FileBackedTasksManager.
                reader(new File("resources/newFileBackedTaskManager.csv"));

        System.out.println(testTaskStringsSave);
        System.out.println(testTaskStringsLoad);
        assertEquals(testTaskStringsSave, testTaskStringsLoad);
    }
}