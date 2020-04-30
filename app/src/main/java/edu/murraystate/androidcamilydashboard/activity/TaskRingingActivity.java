package edu.murraystate.androidcamilydashboard.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.databinding.ActivityTaskRingingBinding;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.DateToString;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.TimeToString;

public class TaskRingingActivity extends AppCompatActivity {

    ActivityTaskRingingBinding binding;
    private MediaPlayer mediaPlayer;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskRingingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);


        Task task = (Task) getIntent().getSerializableExtra(TASK);
        binding.tvTitle.setText(task.getTitle());
        binding.tvTime.setText(TimeToString(task.getHour(), task.getMinute()));
        binding.tvDate.setText(DateToString(task.getYear(), task.getMonth(), task.getDay()));

        if (task.getType() == 0)
            binding.ringingLayout.setBackground(getResources().getDrawable(R.drawable.ringing_gradient_0));
        if (task.getType() == 1)
            binding.ringingLayout.setBackground(getResources().getDrawable(R.drawable.ringing_gradient_1));
        if (task.getType() == 2)
            binding.ringingLayout.setBackground(getResources().getDrawable(R.drawable.ringing_gradient_2));

        binding.btnDismiss.setOnClickListener(v -> {
            releaseMediaPlayer();
            releaseMWakeLock();
            finish();
        });


        showUp();
        keepScreenOn();
    }

    private void showUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
            setShowWhenLocked(true);
            setTurnScreenOn(true);

        } else {
            final Window window = getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        setUpPlayer();
    }

    private void setUpPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        mediaPlayer.setScreenOnWhilePlaying(true);
    }

    private void keepScreenOn() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        mWakeLock.acquire(2 * 60 * 1000L);
    }

    private void releaseMWakeLock() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        releaseMWakeLock();
        releaseMediaPlayer();
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        releaseMWakeLock();
        releaseMediaPlayer();
        super.onBackPressed();
    }
}
