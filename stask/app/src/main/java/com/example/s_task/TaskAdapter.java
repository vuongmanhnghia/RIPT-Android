package com.example.s_task;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task, int position);
        void onTaskDelete(Task task, int position);
        void onTaskStatusChanged(Task task, int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxCompleted;
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewDueDate;
        private ImageButton buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Task task) {
            textViewName.setText(task.getName());
            textViewDescription.setText(task.getDescription());
            textViewDueDate.setText("Hạn: " + task.getDueDate());
            checkBoxCompleted.setChecked(task.isCompleted());

            // Strike through text if completed
            if (task.isCompleted()) {
                textViewName.setPaintFlags(textViewName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewName.setPaintFlags(textViewName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Click to edit
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task, getAdapterPosition());
                }
            });

            // Delete button
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDelete(task, getAdapterPosition());
                }
            });

            // Checkbox for completion status
            checkBoxCompleted.setOnClickListener(v -> {
                task.setCompleted(checkBoxCompleted.isChecked());
                if (listener != null) {
                    listener.onTaskStatusChanged(task, getAdapterPosition());
                }
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}

