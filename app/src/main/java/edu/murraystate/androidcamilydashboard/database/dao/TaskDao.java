package edu.murraystate.androidcamilydashboard.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.murraystate.androidcamilydashboard.database.entity.Task;

@Dao
public interface TaskDao {

    @Insert
    long addTask(Task task);

    @Update
    void editTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks_table WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks_table WHERE year = :year AND month = :month AND day = :day AND type = :type ORDER BY year ASC, month ASC, day ASC, hour ASC, minute ASC")
    List<Task> getTaskByDate(int year, int month, int day, int type);
}
