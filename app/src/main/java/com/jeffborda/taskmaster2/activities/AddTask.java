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

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTask";
    private TaskmasterDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name)).allowMainThreadQueries().build();

        TextView taskCounter = findViewById(R.id.addtask_task_counter);
        //TODO: Change the int variable that is being appended to the TextView taskCounter to reflect the actual number in database
        taskCounter.append(Integer.toString(0));

        final Button addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                // Hide keyboard on button tap
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
                    addNewTask();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_task_added_success),Toast.LENGTH_LONG).show();
                    // Send back to MainActivity after adding a Task
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

        // Call method to add to local database
        this.saveTaskToLocalDatabse(title, description);

        // Call method to add to cloud database
        this.saveTaskToCloudDatabase(title, description);
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

//        try (Response response = okHttpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//                System.out.println(response.body().string());
//        } catch (IOException e) {
//            Log.e(TAG, "Error Posting to Cloud Database");
//            Log.e(TAG, e.getMessage());
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_error_posting_to_cloud_database),Toast.LENGTH_LONG).show();
//        }
    }


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
//                    addTaskActivityInstance.renderTasksListFromHttpData((String)inputMessage.obj);
                }
            };
            Message completeMessage = handlerForMainThread.obtainMessage(0, responseBody);
            completeMessage.sendToTarget();
        }
    }
}
