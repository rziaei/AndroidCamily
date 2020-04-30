package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.murraystate.androidcamilydashboard.databinding.NewActivityPersonalBinding;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK_MANAGER_TITLE;
import static edu.murraystate.androidcamilydashboard.util.Const.TASK_TYPE;

public class PersonalActivity extends AppCompatActivity {

    NewActivityPersonalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewActivityPersonalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.rellayWorkout.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskManagementActivity.class)
                    .putExtra(TASK_MANAGER_TITLE, "Work out")
                    .putExtra(TASK_TYPE, 0)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });

        binding.rellayBeamed.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskManagementActivity.class)
                    .putExtra(TASK_MANAGER_TITLE, "Beauty & health")
                    .putExtra(TASK_TYPE, 1)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });

        binding.rellayFriMeet.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskManagementActivity.class)
                    .putExtra(TASK_MANAGER_TITLE, "Friends & meetings")
                    .putExtra(TASK_TYPE, 2)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });


    }
}
