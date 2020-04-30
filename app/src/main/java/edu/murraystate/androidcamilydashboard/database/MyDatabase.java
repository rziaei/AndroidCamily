package edu.murraystate.androidcamilydashboard.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.murraystate.androidcamilydashboard.database.dao.TaskDao;
import edu.murraystate.androidcamilydashboard.database.entity.Task;

import static edu.murraystate.androidcamilydashboard.util.Const.DATABASE_NAME;

@Database(entities = {Task.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    private static MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context, MyDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract TaskDao taskDao();
}
