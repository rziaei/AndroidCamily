package edu.murraystate.androidcamilydashboard.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.repository.TaskRepository;
import edu.murraystate.androidcamilydashboard.util.Date;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    private MutableLiveData<List<Task>> tasksByDate;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = TaskRepository.getInstance(application);
        tasksByDate = taskRepository.getTasksByDate();
    }

    public void add(Task task) {
        taskRepository.addTask(task);
    }

    public void edit(Task task) {
        taskRepository.editTask(task);
    }

    public void delete(Task task) {
        taskRepository.deleteTask(task);
    }

    public MutableLiveData<List<Task>> getTasksByDate() {
        return tasksByDate;
    }

    public void FilterByDate(Date date, int type) {
        taskRepository.FilterByDate(date,type);
    }
}
