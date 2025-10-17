package com.example.s_task;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FloatingActionButton fabAdd;
    private TextView textViewEmpty;
    private TaskStorage taskStorage;

    private String currentFilter = "all";

    private ActivityResultLauncher<Intent> addTaskLauncher;
    private ActivityResultLauncher<Intent> editTaskLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Activity Result Launchers
        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task task = (Task) result.getData().getSerializableExtra("task");
                        if (task != null) {
                            taskList.add(task);
                            saveTasks();
                            updateUI();
                        }
                    }
                });

        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task task = (Task) result.getData().getSerializableExtra("task");
                        if (task != null) {
                            for (int i = 0; i < taskList.size(); i++) {
                                if (taskList.get(i).getId().equals(task.getId())) {
                                    taskList.set(i, task);
                                    break;
                                }
                            }
                            saveTasks();
                            updateUI();
                        }
                    }
                });

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAdd = findViewById(R.id.fabAdd);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        // Initialize storage
        taskStorage = new TaskStorage(this);

        // Load tasks
        taskList = new ArrayList<>();
        loadTasks();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        // FAB click listener
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            addTaskLauncher.launch(intent);
        });

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            addTaskLauncher.launch(intent);
            return true;
        } else if (id == R.id.menu_delete_all) {
            showDeleteAllDialog();
            return true;
        } else if (id == R.id.menu_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_filter_all) {
            currentFilter = "all";
            applyFilter();
            return true;
        } else if (id == R.id.menu_filter_completed) {
            currentFilter = "completed";
            applyFilter();
            return true;
        } else if (id == R.id.menu_filter_pending) {
            currentFilter = "pending";
            applyFilter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tất cả")
                .setMessage("Bạn có chắc muốn xóa tất cả công việc?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    taskList.clear();
                    saveTasks();
                    updateUI();
                    Toast.makeText(this, "Đã xóa tất cả công việc", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void applyFilter() {
        List<Task> filteredList = new ArrayList<>();

        if (currentFilter.equals("all")) {
            filteredList.addAll(taskList);
            setTitle("S-Task - Tất cả");
        } else if (currentFilter.equals("completed")) {
            for (Task task : taskList) {
                if (task.isCompleted()) {
                    filteredList.add(task);
                }
            }
            setTitle("S-Task - Đã hoàn thành");
        } else if (currentFilter.equals("pending")) {
            for (Task task : taskList) {
                if (!task.isCompleted()) {
                    filteredList.add(task);
                }
            }
            setTitle("S-Task - Chưa hoàn thành");
        }

        taskAdapter.updateTasks(filteredList);
        updateEmptyView(filteredList);
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(taskStorage.loadTasks());
    }

    private void saveTasks() {
        taskStorage.saveTasks(taskList);
    }

    private void updateUI() {
        applyFilter();
    }

    private void updateEmptyView(List<Task> displayList) {
        if (displayList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskClick(Task task, int position) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("task", task);
        editTaskLauncher.launch(intent);
    }

    @Override
    public void onTaskDelete(Task task, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc muốn xóa công việc này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    taskList.remove(task);
                    saveTasks();
                    updateUI();
                    Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onTaskStatusChanged(Task task, int position) {
        saveTasks();
        updateUI();
        Toast.makeText(this, task.isCompleted() ? "Đã hoàn thành" : "Chưa hoàn thành",
                Toast.LENGTH_SHORT).show();
    }
}