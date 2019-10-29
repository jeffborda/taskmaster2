package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.adapters.TaskItemAdapter;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
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


        // Http Get Request
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .build();
        okHttpClient.newCall(request).enqueue(new LogHttpDataCallback(this));

        // RecyclerView Setup
        //TODO: this can be commented back in when rendering Tasks from local database
//        this.renderRecyclerViewFromDatabase();


        // Button Setup
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

        this.renderRecyclerViewFromDatabase();
    }

    @Override
    public void onTaskSelected(Task task) {
        Log.i(TAG, "RecyclerView TextView clicked on this Task: " + task);
        Intent taskDetailsIntent = new Intent(this, TaskDetails.class);
        // When a Task is clicked, put its ID as an extra so it can be pulled out of database on TaskDetails activity
        taskDetailsIntent.putExtra("task_id", task.getId());
        MainActivity.this.startActivity(taskDetailsIntent);
    }

    private void renderRecyclerViewFromDatabase() {
        // Build the database and instantiate the List that hold the tasks from database
        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name))
                // fallbackToDestructiveMigration will delete all entries from the database if the schema changes
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        this.tasks = new LinkedList<>();

        // Get everything from database and put in list of tasks to be rendered by recycler view
        this.tasks.addAll(this.database.taskDao().getAll());

        // Re android doc: https://developer.android.com/guide/topics/ui/layout/recyclerview
        // Render Task items to the screen with RecyclerView
        RecyclerView recyclerView = findViewById(R.id.task_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Define Adapter class that is able to communicate with RecyclerView
        recyclerView.setAdapter(new TaskItemAdapter(this.tasks, this));
    }

    public void renderTasksListFromHttpData(String httpResponseData) {
        this.tasks = new LinkedList<>();
        Gson gson = new Gson();
        Type typeOf = new TypeToken<LinkedList<Task>>(){}.getType();
        this.tasks = gson.fromJson(httpResponseData, typeOf);
        RecyclerView recyclerView = findViewById(R.id.task_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskItemAdapter(this.tasks, this));
    }

    class LogHttpDataCallback implements Callback {

        private MainActivity mainActivityInstance;
        private final static String TAG = "LogHttpDataCallback";

        LogHttpDataCallback(MainActivity mainActivityInstance) {
            this.mainActivityInstance = mainActivityInstance;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.e(TAG, "Internet Error");
            Log.e(TAG, e.getMessage());
        }

        // onResponse is called by OkHttp if the request is successful.
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String responseBody = response.body().string();
            Log.i(TAG, "Http Response: " + responseBody);

            // Now you can call another method in the MainActivity class
            //mainActivityInstance.renderHttpData(responseBody);

            // Handler allows us to pass info from another thread back to the main thread.
            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    mainActivityInstance.renderTasksListFromHttpData((String)inputMessage.obj);
                }
            };
            Message completeMessage = handlerForMainThread.obtainMessage(0, responseBody);
            completeMessage.sendToTarget();
        }
    }
}
