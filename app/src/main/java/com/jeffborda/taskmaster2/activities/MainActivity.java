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

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.OnCreateTaskSubscription;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.adapters.TaskItemAdapter;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskState;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity implements TaskItemAdapter.OnTaskSelectedListener {

    private static final String TAG = "jtb.MainActivity";
    private TaskmasterDatabase database;
    private TaskItemAdapter taskItemAdapter;
    private RecyclerView recyclerView;
    private List<Task> tasks;
    private AWSAppSyncClient awsAppSyncClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView Setup
        this.tasks = new LinkedList<>();
        this.recyclerView = findViewById(R.id.task_items_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.taskItemAdapter = new TaskItemAdapter(this.tasks, this);
        this.recyclerView.setAdapter(this.taskItemAdapter);

        // Room Database Build
        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name)).allowMainThreadQueries().build();

        // AWS Build Setup. RE: https://aws-amplify.github.io/docs/android/start?ref=amplify-android-btn
        this.awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // AWS - Subscribe to future updates
        OnCreateTaskSubscription subscription = OnCreateTaskSubscription.builder().build();
        this.awsAppSyncClient.subscribe(subscription).execute(new AppSyncSubscriptionCall.Callback<OnCreateTaskSubscription.Data>() {
            private static final String TAG = "jtb.Subscription";
            // AWS calls this method when a new Task is created
            @Override
            public void onResponse(@Nonnull com.apollographql.apollo.api.Response<OnCreateTaskSubscription.Data> response) {
                //TODO: Verify constructor usage, esp. TaskState
                Task task = new Task(response.data().onCreateTask().title(), response.data().onCreateTask().description(), TaskState.valueOf(response.data().onCreateTask().taskState().toString()));
                MainActivity.this.taskItemAdapter.addItem(task);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "Failure in subscription callback: " + e.getMessage());
            }

            // Called after successful subscription attempt
            @Override
            public void onCompleted() {
                Log.i(TAG, "Subscription successful.");
            }
        });

        // Calls the Get All Tasks query - returns a list
        this.runGetAllTasksQuery();


        // Get Tasks from backend site - Http Get Request
        //this.okHttpGetRequestToBackend();

        // Get Tasks from Room database RecyclerView
        //this.renderRecyclerViewFromRoomDatabase();


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
        super.onResume();

        this.setUsername();
        this.runGetAllTasksQuery();
    }

    @Override
    public void onTaskSelected(Task task) {
        Log.i(TAG, "RecyclerView TextView clicked on this Task: " + task);
        Intent taskDetailsIntent = new Intent(this, TaskDetails.class);
        // When a Task is clicked, put its ID as an extra so it can be pulled out of database on TaskDetails activity
        taskDetailsIntent.putExtra("task_id", task.getId());
        MainActivity.this.startActivity(taskDetailsIntent);
    }

    private void setUsername() {
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


    // TODO: Now this should be changed to synchronize the local Room database with any changes from the cloud database
    private void renderRecyclerViewFromRoomDatabase() {
        // Build the database and instantiate the List that hold the tasks from database
        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name))
                // fallbackToDestructiveMigration will delete all entries from the database if the schema changes
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Get everything from database and put in list of tasks to be rendered by recycler view
        this.tasks.addAll(this.database.taskDao().getAll());
        this.taskItemAdapter.notifyDataSetChanged();
    }

    public void okHttpGetRequestToBackend() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .build();
        okHttpClient.newCall(request).enqueue(new LogHttpDataCallback(this));
    }

    //TODO: This should be refactored if used again
    public void renderTasksListFromHttpData(String httpResponseData) {
        this.tasks = new LinkedList<>();
        Gson gson = new Gson();
        Type typeOf = new TypeToken<LinkedList<Task>>(){}.getType();
        this.tasks = gson.fromJson(httpResponseData, typeOf);
        this.recyclerView = findViewById(R.id.task_items_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(new TaskItemAdapter(this.tasks, this));
    }

    // GraphQL - Query the dynamo database - returns list of Tasks
    public void runGetAllTasksQuery(){
        awsAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(getAllTasksCallback);
    }

    private GraphQLCall.Callback<ListTasksQuery.Data> getAllTasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {

        private static final String TAG = "jtb.getAllTasksCallback";

        // Instance methods on the anon inner class
        @Override
        public void onResponse(@Nonnull final com.apollographql.apollo.api.Response<ListTasksQuery.Data> response) {
            Log.i(TAG, "All tasks query: " + response.data().listTasks().items().toString());
            Log.i(TAG, "All tasks query size: " + response.data().listTasks().items().size());
            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    List<ListTasksQuery.Item> items = response.data().listTasks().items();
                    // Clear the "old" list
                    MainActivity.this.tasks.clear();
                    // Go through the list of items we get back from dynamo db
                    for(ListTasksQuery.Item item : items) {
                        // Reconstruct the Tasks using the Task(ListTasksQuery.Item) constructor
                        MainActivity.this.tasks.add(new Task(item));
                    }
                    MainActivity.this.taskItemAdapter.notifyDataSetChanged();

                }
            };
            handlerForMainThread.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, "Error retrieving all tasks: " + e.getMessage());
        }
    };


    class LogHttpDataCallback implements Callback {

        private MainActivity mainActivityInstance;
        private static final String TAG = "jtb.LogHttpDataCallback";

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

            // Now you could call another method in the MainActivity class
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
