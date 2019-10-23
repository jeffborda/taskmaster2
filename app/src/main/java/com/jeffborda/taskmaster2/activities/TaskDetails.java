package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("taskTitle", "Task Title");
        TextView titleHeadingTextView = findViewById(R.id.heading_task_details);
        TextView titleTextView = findViewById(R.id.task_details_title);
        titleHeadingTextView.setText(title);
        titleTextView.setText(title);
    }
}
