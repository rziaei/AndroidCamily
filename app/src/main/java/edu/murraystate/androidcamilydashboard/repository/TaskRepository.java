package edu.murraystate.androidcamilydashboard.repository;


import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.murraystate.androidcamilydashboard.database.MyDatabase;
import edu.murraystate.androidcamilydashboard.database.dao.TaskDao;
import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.service.TaskAlarmReceiver;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK_ID;


public class TaskRepository {

    private static TaskRepository instance;
    private static TaskDao taskDao;
    private static Application application;
    private MutableLiveData<List<Task>> tasksByDate = new MutableLiveData<>();

    public static TaskRepository getInstance(Application application) {
        if (instance == null) {
            taskDao = MyDatabase.getInstance(application).taskDao();
            instance = new TaskRepository();
        }
        TaskRepository.application = application;
        return instance;
    }

    public static void ADD_ALARM(Task task, int taskId, Application application) {
            //import date to calender object
            Calendar currentCalendar = Calendar.getInstance();
            Calendar taskCalendar = (Calendar) currentCalendar.clone();
            taskCalendar.set(Calendar.HOUR_OF_DAY, task.getHour());
            taskCalendar.set(Calendar.MINUTE, task.getMinute());
            taskCalendar.set(Calendar.SECOND, 0);
            taskCalendar.set(Calendar.YEAR, task.getYear());
            taskCalendar.set(Calendar.MONTH,task.getMonth() -1 );
            taskCalendar.set(Calendar.DAY_OF_MONTH,  task.getDay());
            //compare dates
            Date taskDateAndTime = taskCalendar.getTime();
            Date currentDateAndTime = currentCalendar.getTime();
            if (!taskDateAndTime.before(currentDateAndTime)) {
                Log.e("mmm", "ADD_ALARM: date and time is ok");
                AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(application, TaskAlarmReceiver.class);
                intent.putExtra(TASK_ID, taskId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(application, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (alarmManager != null) {
                    Log.e("mmm", "ADD_ALARM: alarmManager is ok");
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(taskCalendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                }
            }

    }

    public static void CANCEL_ALARM(int id, Application application) {
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(application, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void addTask(Task task) {
        new addTaskAsyncTask(taskDao).execute(task);
    }

    public void editTask(Task task) {
        new editTaskAsyncTask(taskDao).execute(task);
    }

    public void deleteTask(Task task) {
        new deleteTaskAsyncTask(taskDao).execute(task);
    }

    public MutableLiveData<List<Task>> getTasksByDate() {
        return tasksByDate;
    }

    private void FilterByDateFinished(List<Task> result) {
        tasksByDate.setValue(result);
    }

    public void FilterByDate(edu.murraystate.androidcamilydashboard.util.Date date, int type) {
        FilterByDateAsyncTask task = new FilterByDateAsyncTask(taskDao,type);
        task.delegate = this;
        task.execute(date);
    }


    private static class addTaskAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao taskDao;

        addTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            long taskId = taskDao.addTask(tasks[0]);

            ADD_ALARM(tasks[0], (int) taskId, application);

            return null;
        }
    }

    private static class editTaskAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao taskDao;

        private editTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.editTask(tasks[0]);

            CANCEL_ALARM(tasks[0].getId(), application);
            ADD_ALARM(tasks[0], tasks[0].getId(), application);

            return null;
        }
    }

    private static class FilterByDateAsyncTask extends AsyncTask<edu.murraystate.androidcamilydashboard.util.Date, Void, List<Task>> {

        private TaskDao taskDao;
        private TaskRepository delegate = null;
        private int type;

        FilterByDateAsyncTask(TaskDao taskDao, int type) {
            this.taskDao = taskDao;
            this.type = type;
        }

        @Override
        protected final List<Task> doInBackground(edu.murraystate.androidcamilydashboard.util.Date... dates) {
          return taskDao.getTaskByDate(dates[0].getYear(), dates[0].getMonth(), dates[0].getDay(), type);
        }

        @Override
        protected void onPostExecute(List<Task> result) {
            delegate.FilterByDateFinished(result);
        }
    }

    private static class deleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao taskDao;

        private deleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.deleteTask(tasks[0]);

            CANCEL_ALARM(tasks[0].getId(), application);

            return null;
        }

    }

}
