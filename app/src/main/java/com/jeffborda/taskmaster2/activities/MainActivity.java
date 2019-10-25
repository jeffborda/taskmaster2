package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.adapters.TaskItemAdapter;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskItemAdapter.OnTaskSelectedListener {

    private static final String TAG = "MainActivity";
    private TaskmasterDatabase database;
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, "taskmaster_database").allowMainThreadQueries().build();
        this.tasks = new LinkedList<>();

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

        // TODO: Setup the RecyclerView
        // Give the tasks List a few Tasks to render
        Task task1 = new Task("Take out garbage", "Recycling and compost");
//        Task task2 = new Task("Grade labs", "By 8:00pm");
//        Task task3 = new Task("Give Scout a bath", "Tomorrow");

        // Get everything from database and to the list of tasks to be rendered by recycler view
        this.tasks.addAll(this.database.taskDao().getAll());



        // Re: https://developer.android.com/guide/topics/ui/layout/recyclerview
        // Render Task items to the screen with RecyclerView
        RecyclerView recyclerView = findViewById(R.id.task_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Define Adapter class that is able to communicate with RecyclerView
        recyclerView.setAdapter(new TaskItemAdapter(this.tasks, this));



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

    @Override
    public void onTaskSelected(Task task) {
        Log.i(TAG, "RecyclerView TextView clicked on this Task: " + task);
        Intent taskDetailsIntent = new Intent(this, TaskDetails.class);
        taskDetailsIntent.putExtra("taskTitle", task.getTitle());
        taskDetailsIntent.putExtra("taskDescription", task.getDescription());
        MainActivity.this.startActivity(taskDetailsIntent);
    }
}
