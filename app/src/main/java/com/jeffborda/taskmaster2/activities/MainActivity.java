package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference the button using its ID
        // Add event listener to the button
        Button addTaskButton = findViewById(R.id.main_activity_button_add_task);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "'Add Task' button clicked.");
                Intent addTaskIntent = new Intent(MainActivity.this, AddTask.class);
                startActivity(addTaskIntent);
            }
        });

        Button allTasksButton = findViewById(R.id.main_activity_button_all_tasks);
        allTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "'All Tasks' button clicked.");
                Intent allTasksIntent = new Intent(MainActivity.this, AllTasks.class);
                startActivity(allTasksIntent);
            }
        });

        Button settingsButton = findViewById(R.id.main_activity_button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "'Settings' button clicked.");
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
            }
        });


        // PLACEHOLDER TASKS FOR 3 BUTTONS
        final Button taskButton1 = findViewById(R.id.placeholder_task_button1);
        final Button taskButton2 = findViewById(R.id.placeholder_task_button2);
        final Button taskButton3 = findViewById(R.id.placeholder_task_button3);
        Task task1 = new Task("Take out garbage", "Recycling and compost");
        Task task2 = new Task("Grade labs", "By 8:00pm");
        Task task3 = new Task("Give Scout a bath", "Tomorrow");
        this.tasks = new LinkedList<>();
        this.tasks.add(task1);
        this.tasks.add(task2);
        this.tasks.add(task3);
        taskButton1.setText(task1.getTitle());
        taskButton2.setText(task2.getTitle());
        taskButton3.setText(task3.getTitle());

        taskButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "Task1 button clicked.");
                Intent taskDetailsIntent = new Intent(MainActivity.this, TaskDetails.class);
                taskDetailsIntent.putExtra("taskTitle", taskButton1.getText().toString());
                startActivity(taskDetailsIntent);
            }
        });

        taskButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "Task2 button clicked.");
                Intent taskDetailsIntent = new Intent(MainActivity.this, TaskDetails.class);
                taskDetailsIntent.putExtra("taskTitle", taskButton2.getText().toString());
                startActivity(taskDetailsIntent);
            }
        });

        taskButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Log.i(TAG, "Task3 button clicked.");
                Intent taskDetailsIntent = new Intent(MainActivity.this, TaskDetails.class);
                taskDetailsIntent.putExtra("taskTitle", taskButton3.getText().toString());
                startActivity(taskDetailsIntent);
            }
        });






    }

    @Override
    public void onResume() {
        // Required to override the method
        super.onResume();
        // Setup default Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Get username from Shared Preferences
        String username = sharedPreferences.getString("username", "user");
        // Reference to the heading TextView on MainActivity
        TextView mainActivityHeading = findViewById(R.id.heading_main_activity);
        if(username.equals("user")) {
            // Set the Heading on MainActivity if no username in SharedPreferences
            mainActivityHeading.setText(getResources().getString(R.string.heading_without_username_main_activity));
        }
        else {
            mainActivityHeading.setText(username + getResources().getString(R.string.heading_with_username_main_activity));
        }


    }

    public void goToTaskDetails(View view) {

    }
}
