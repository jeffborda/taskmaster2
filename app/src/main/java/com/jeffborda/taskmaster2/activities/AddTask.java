package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }


    public void addNewTask() {
        EditText titleEditText = findViewById(R.id.add_task_title);
        EditText descriptionEditText = findViewById(R.id.add_task_description);
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        Task task = new Task(title, description);
        //TODO: Need to do something with the new Task, like save it to a database.
    }
}
