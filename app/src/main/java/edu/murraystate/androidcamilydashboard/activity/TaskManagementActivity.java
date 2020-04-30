package edu.murraystate.androidcamilydashboard.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.adapters.TaskAdapter;
import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.databinding.ActivityTaskManagementBinding;
import edu.murraystate.androidcamilydashboard.util.Date;
import edu.murraystate.androidcamilydashboard.viewModel.TaskViewModel;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK;
import static edu.murraystate.androidcamilydashboard.util.Const.TASK_MANAGER_TITLE;
import static edu.murraystate.androidcamilydashboard.util.Const.TASK_TYPE;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.StringToDate;

public class TaskManagementActivity extends AppCompatActivity implements TaskAdapter.TaskListener, DatePickerDialog.OnDateSetListener {

    ActivityTaskManagementBinding binding;
    TaskViewModel taskViewModel;
    TaskAdapter taskAdapter;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskManagementBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String title = getIntent().getExtras().getString(TASK_MANAGER_TITLE);
        type = getIntent().getExtras().getInt(TASK_TYPE);


        if (type == 0)
            binding.background.setBackgroundColor(getResources().getColor(R.color.colorMintLight));
        if (type == 1)
            binding.background.setBackgroundColor(getResources().getColor(R.color.colorGrapeFruitLight));
        if (type == 2)
            binding.background.setBackgroundColor(getResources().getColor(R.color.colorFlowerLight));

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        binding.toolbar.tvToolbarTitle.setText(title);

        binding.toolbar.cvDate.setOnClickListener(v -> {
            int[] date = StringToDate(binding.toolbar.tvDate.getText().toString().toLowerCase().trim());
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, date[0], date[1] - 1, date[2]);
            datePickerDialog.show();
        });

        taskAdapter = new TaskAdapter(this);
        RecyclerView rvTask = binding.rvTask;
        rvTask.setHasFixedSize(true);
        rvTask.setAdapter(taskAdapter);

        binding.fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTaskActivity.class).putExtra(TASK_TYPE, type).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });

    }

    @Override
    public void onTaskOptionsClick(Task task, CardView view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.task_options_menu);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_edit:
                    startActivity(new Intent(TaskManagementActivity.this, EditTaskActivity.class).putExtra(TASK, task).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    return true;

                case R.id.item_delete:
                    taskViewModel.delete(task);
                    setList(new Date(task.getYear(), task.getMonth(), task.getDay()), task.getType());
                    taskAdapter.notifyDataSetChanged();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date date = new Date(year, month + 1, dayOfMonth);
        binding.toolbar.tvDate.setText(date.toString());
        setList(date, type);
    }

    private void setList(Date date, int type) {
        taskViewModel.FilterByDate(date, type);
        taskViewModel.getTasksByDate().observe(this, tasks -> {
            taskAdapter.submitList(tasks);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        Date today = new Date(year, month + 1, day);
        setList(today, type);
        binding.toolbar.tvDate.setText(today.toString());
    }
}
