package edu.murraystate.androidcamilydashboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import edu.murraystate.androidcamilydashboard.activity.TaskRingingActivity;
import edu.murraystate.androidcamilydashboard.database.MyDatabase;
import edu.murraystate.androidcamilydashboard.database.entity.Task;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK;
import static edu.murraystate.androidcamilydashboard.util.Const.TASK_ID;

public class TaskAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras == null) {
            throw new NullPointerException(TASK_ID + " is null");
        }
        int taskId = extras.getInt(TASK_ID);

        Task task = MyDatabase.getInstance(context).taskDao().getTaskById(taskId);


        createNotification(context, task);
        showRingingActivity(context, task);
    }

    private void createNotification(Context context, Task task) {
        TransactionNotificationHelper transactionNotificationHelper = new TransactionNotificationHelper(context);
        NotificationCompat.Builder builder = transactionNotificationHelper.builder(task);
        transactionNotificationHelper.getTransactionNotificationManager().notify(task.getId(), builder.build());
    }

    private void showRingingActivity(Context context, Task task) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TRAININGCOUNTDOWN");
        wakeLock.acquire(2 * 60 * 1000L);

        Intent intent = new Intent(context, TaskRingingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TASK, task);
        context.startActivity(intent);
        wakeLock.release();
    }
}
