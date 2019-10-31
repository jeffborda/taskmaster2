package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import type.CreateTaskInput;
import type.TaskState;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;
import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "jtb.AddTask";
    private TaskmasterDatabase database;
    private AWSAppSyncClient awsAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Room Database Build
        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name)).allowMainThreadQueries().build();

        // AWS Build Setup. RE: https://aws-amplify.github.io/docs/android/start?ref=amplify-android-btn
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        TextView taskCounter = findViewById(R.id.addtask_task_counter);
        //TODO: Change the int variable that is being appended to the TextView taskCounter to reflect the actual number in database
        taskCounter.append(Integer.toString(0));

        Button addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                // Hide keyboard on button click
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                Log.i(TAG, "Add Task button clicked.");
                EditText titleEditText = findViewById(R.id.add_task_title_field);
                if(TextUtils.isEmpty(titleEditText.getText())) {
                    Log.i(TAG, "Title field empty, no task added.");
                    titleEditText.setError("Add a Title");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_no_title),Toast.LENGTH_LONG).show();
                }
                else {
                    AddTask.this.addNewTask();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_task_added_success),Toast.LENGTH_LONG).show();
                    AddTask.this.finish();
                }
            }
        });
    }


    public void addNewTask() {
        Log.i(TAG, "addNewTask() method called");
        EditText titleEditText = findViewById(R.id.add_task_title_field);
        EditText descriptionEditText = findViewById(R.id.add_task_description_field);
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // Call method to add to dynamo db
        this.runAddTaskMutation(title, description);
        /*
        // Call method to add to local database
        this.saveTaskToLocalDatabse(title, description);

        // Call method to add to cloud database
        this.saveTaskToCloudDatabase(title, description);
         */
    }

    public void saveTaskToLocalDatabse(String title, String description) {
        Task task = new Task(title, description);
        // Add the new Task to the database
        database.taskDao().addTask(task);
        Log.i(TAG, String.format("Made new Task: %s", task));
    }

    public void saveTaskToCloudDatabase(String title, String description) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("body", description)
                .build();

        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new AddTask.LogHttpDataCallback());
    }

    // GraphQL Mutation to add a new Task to the database
    public void runAddTaskMutation(String title, String description) {
        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .description(description)
                .taskState(TaskState.NEW)
                .build();
        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(addTaskMutationCallback);
    }

    // GraphQL Add Task Callback
    public GraphQLCall.Callback<CreateTaskMutation.Data> addTaskMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {

        private static final String TAG = "jtb.addTaskMutation";

        @Override
        public void onResponse(@Nonnull com.apollographql.apollo.api.Response<CreateTaskMutation.Data> response) {
            Log.i(TAG, "Added new Task to GraphQL database!");
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, "Error adding to GraphQL database: " + e.getMessage());
        }
    };


    class LogHttpDataCallback implements Callback {

        private final static String TAG = "AddTask.LogHttpData";

//        LogHttpDataCallback(AddTask addTaskActivityInstance) {
//            this.addTaskActivityInstance = addTaskActivityInstance;
//        }

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
                    //TODO: Don't need to do anything here?
                }
            };
            Message completeMessage = handlerForMainThread.obtainMessage(0, responseBody);
            completeMessage.sendToTarget();
        }
    }
}
