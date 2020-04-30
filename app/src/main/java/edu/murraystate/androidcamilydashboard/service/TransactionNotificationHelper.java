package edu.murraystate.androidcamilydashboard.service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.database.entity.Task;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK_CHANNEL_ID;
import static edu.murraystate.androidcamilydashboard.util.Const.TASK_CHANNEL_NAME;

public class TransactionNotificationHelper extends ContextWrapper {

    private NotificationManager transactionNotificationManager;

    public TransactionNotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(TASK_CHANNEL_ID,
                TASK_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        getTransactionNotificationManager().createNotificationChannel(channel);
    }

    public NotificationManager getTransactionNotificationManager() {
        if (transactionNotificationManager == null) {
            transactionNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return transactionNotificationManager;
    }

    public NotificationCompat.Builder builder(Task task) {
        return new NotificationCompat.Builder(getApplicationContext(), TASK_CHANNEL_ID)
                .setContentTitle("Alarm")
                .setContentText(task.getTitle())
                .setSmallIcon(R.drawable.ic_alarm);
    }
}
