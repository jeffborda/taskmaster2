package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TextView taskCounter = findViewById(R.id.addtask_task_counter);
        //TODO: Change the int variable that is being appended to the TextView taskCounter
        taskCounter.append(Integer.toString(0));

        final Button addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                EditText titleEditText = findViewById(R.id.add_task_title_field);
                if(TextUtils.isEmpty(titleEditText.getText())) {
                    titleEditText.setError("Add a Title");
                    Toast.makeText(getApplicationContext(),"Tasks must have a title.",Toast.LENGTH_LONG).show();
                }
                else {
                    addNewTask();
                }
            }
        });
    }


    public void addNewTask() {
        EditText titleEditText = findViewById(R.id.add_task_title_field);
        EditText descriptionEditText = findViewById(R.id.add_task_description_field);
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        Task task = new Task(title, description);
        //TODO: Need to do something with the new Task, like save it to a database.
    }
}
