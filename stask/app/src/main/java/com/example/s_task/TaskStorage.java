package com.example.s_task;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String KEY_TASKS = "tasks";
    private SharedPreferences sharedPreferences;

    public TaskStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveTasks(List<Task> tasks) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Task task : tasks) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", task.getId());
                jsonObject.put("name", task.getName());
                jsonObject.put("description", task.getDescription());
                jsonObject.put("dueDate", task.getDueDate());
                jsonObject.put("completed", task.isCompleted());
                jsonArray.put(jsonObject);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_TASKS, jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        String jsonString = sharedPreferences.getString(KEY_TASKS, "");

        if (!jsonString.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Task task = new Task(
                            jsonObject.getString("id"),
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            jsonObject.getString("dueDate")
                    );
                    task.setCompleted(jsonObject.getBoolean("completed"));
                    tasks.add(task);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tasks;
    }

    public void clearAllTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TASKS);
        editor.apply();
    }
}

