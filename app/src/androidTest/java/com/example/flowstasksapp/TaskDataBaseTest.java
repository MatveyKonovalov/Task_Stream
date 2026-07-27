package com.example.flowstasksapp;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.flowstasksapp.data.database.daos.TaskDao;
import com.example.flowstasksapp.data.database.TaskDateBase;
import com.example.flowstasksapp.data.database.entities.TaskEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TaskDataBaseTest {
    private TaskDateBase database;
    private TaskDao taskDao;

    @Before // До всех тестов создаём бд в памяти
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(
                context,
                TaskDateBase.class
        ).allowMainThreadQueries().build();
        taskDao = database.taskDao();
    }

    @After
    public void tearDown() {
        if (database != null) {
            database.close();
        }
    }

    @Test
    public void testInsertAbdGetTask() throws InterruptedException {
        // Given
        TaskEntity task = new TaskEntity(0,
                "Test Task",
                "Test DEscription",
                "Test",
                "Test",
                "30.06.2007");
        var taskId =  taskDao.addTaskTest(task);
        assertTrue(taskId > 0);

    }
}
