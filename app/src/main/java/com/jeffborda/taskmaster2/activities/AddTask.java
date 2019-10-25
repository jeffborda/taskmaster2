package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        Task task = new Task(title, description);
        // Add the new Task to the database
        database.taskDao().addTask(task);

        Log.i(TAG, String.format("Made new Task: %s", task));
        //TODO: Save the task somewhere
    }
}
