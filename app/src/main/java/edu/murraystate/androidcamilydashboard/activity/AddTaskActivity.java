package edu.murraystate.androidcamilydashboard.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Objects;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.databinding.ActivityAddTaskBinding;
import edu.murraystate.androidcamilydashboard.util.Date;
import edu.murraystate.androidcamilydashboard.viewModel.TaskViewModel;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK_TYPE;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.StringToDate;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.StringToTime;
import static edu.murraystate.androidcamilydashboard.util.TaskBindingHelper.TimeToString;

public class AddTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private ActivityAddTaskBinding binding;
    private boolean isDataAvailable;
    private boolean isTimeAvailable;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        type = getIntent().getExtras().getInt(TASK_TYPE);

        binding.tvToolbarTitle.setText("Add");

        //date
        binding.btnDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.show();
        });

        //time
        binding.btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this));
            timePickerDialog.show();
        });

        //summit
        binding.cvSubmit.setOnClickListener(v -> {
            if (checkInputs()) {
                saveToDatabase();
                finish();
            }
        });

    }

    private boolean checkInputs() {
        boolean result = true;

        //error title
        if (Objects.requireNonNull(binding.etDescription.getText()).toString().trim().length() < 1) {
            binding.etDescription.setError("Can not be empty!");
            result = false;
        }
        //error date
        if (!isDataAvailable) {
            binding.btnDate.setError("Can not be empty!");
            result = false;
        }
        //error time
        if (!isTimeAvailable) {
            binding.btnTime.setError("Can not be empty!");
            result = false;
        }

        return result;
    }

    private void saveToDatabase() {
        if (isDataAvailable && isTimeAvailable) {
            int[] date = StringToDate(binding.btnDate.getText().toString().toLowerCase().trim());
            int[] time = StringToTime(binding.btnTime.getText().toString().toLowerCase().trim());
            Task task = new Task(binding.etDescription.getText().toString().trim(),
                    type,
                    date[0],
                    date[1],
                    date[2],
                    time[0],
                    time[1]);

            TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
            taskViewModel.add(task);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        Date date = new Date(year, month, dayOfMonth);
        isDataAvailable = true;
        binding.btnDate.setError(null);
        binding.btnDate.setText(date.toString());
        binding.btnDate.setTextColor(getResources().getColor(R.color.primaryTextColor));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        isTimeAvailable = true;
        binding.btnTime.setError(null);
        binding.btnTime.setText(TimeToString(hourOfDay, minute));
        binding.btnTime.setTextColor(getResources().getColor(R.color.primaryTextColor));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
