package com.example.s_task;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddEditTaskActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextDueDate;
    private Button buttonSave;
    private Button buttonSelectDate;

    private Task editingTask;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);

        selectedDate = Calendar.getInstance();

        // Check if editing existing task
        Intent intent = getIntent();
        if (intent.hasExtra("task")) {
            editingTask = (Task) intent.getSerializableExtra("task");
            setTitle("Sửa Công Việc");
            populateFields();
        } else {
            setTitle("Thêm Công Việc");
        }

        // Date picker
        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        editTextDueDate.setOnClickListener(v -> showDatePicker());

        // Save button
        buttonSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    editTextDueDate.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void populateFields() {
        if (editingTask != null) {
            editTextName.setText(editingTask.getName());
            editTextDescription.setText(editingTask.getDescription());
            editTextDueDate.setText(editingTask.getDueDate());
        }
    }

    private void saveTask() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dueDate = editTextDueDate.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task;
        if (editingTask != null) {
            task = editingTask;
            task.setName(name);
            task.setDescription(description);
            task.setDueDate(dueDate);
        } else {
            String id = UUID.randomUUID().toString();
            task = new Task(id, name, description, dueDate);
        }

        // Return result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Lưu công việc thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
