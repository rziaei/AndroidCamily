package edu.murraystate.androidcamilydashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.database.entity.Task;
import edu.murraystate.androidcamilydashboard.databinding.TaskItemBinding;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {

            return oldItem.getYear() == newItem.getYear() &&
                    oldItem.getMonth() == newItem.getMonth() &&
                    oldItem.getDay() == newItem.getDay() &&
                    oldItem.getHour() == newItem.getHour() &&
                    oldItem.getMinute() == newItem.getMinute() &&
                    oldItem.getType() == newItem.getType() &&
                    oldItem.getTitle().equals(newItem.getTitle());
        }
    };

    private TaskListener listener;

    public TaskAdapter(Context listener) {
        super(DIFF_CALLBACK);
        this.listener = (TaskListener) listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskItemBinding taskItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.task_item, parent, false);
        return new TaskViewHolder(taskItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.Bind(getCurrentList().get(position));
    }


    public interface TaskListener {
        void onTaskOptionsClick(Task task, CardView cvMore);
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TaskItemBinding binding;

        TaskViewHolder(@NonNull TaskItemBinding TaskItemBinding) {
            super(TaskItemBinding.getRoot());
            this.binding = TaskItemBinding;
        }

        void Bind(Task task) {
            binding.setTask(task);
            MaterialCardView cvMore = binding.cvOptions;
            binding.setCardView(cvMore);
            binding.setListener(listener);
            binding.executePendingBindings();
        }
    }
}
