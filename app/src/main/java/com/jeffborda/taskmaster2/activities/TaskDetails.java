package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;
import com.jeffborda.taskmaster2.models.TaskmasterDatabase;

public class TaskDetails extends AppCompatActivity {

    private TaskmasterDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();

        // TODO: Deal with the case where no matching id was found
        // Get the id from the task that was clicked
        long taskId = intent.getExtras().getLong("task_id");
        // Setup the database
        this.database = Room.databaseBuilder(getApplicationContext(), TaskmasterDatabase.class, getString(R.string.database_name))
                // fallbackToDestructiveMigration will delete all entries from the database if the schema changes
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Make the entry into a Task again from database result
        Task task = this.database.taskDao().getTaskById(taskId);

        EditText titleEditText = findViewById(R.id.task_details_title);
        EditText descriptionEditText = findViewById(R.id.task_details_description);
        TextView statusTextView = findViewById(R.id.task_details_state);
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        statusTextView.setText(task.getTaskState().toString());

        //TODO: These need some other type of listener, like on text change or something
        //On click listeners to display the save button
        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Button saveButton = TaskDetails.this.findViewById(R.id.task_details_save_button);
                saveButton.setVisibility(View.VISIBLE);
            }
        });
        descriptionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Button saveButton = TaskDetails.this.findViewById(R.id.task_details_save_button);
                saveButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
