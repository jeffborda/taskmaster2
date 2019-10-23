package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    @Override
    public void onResume() {
        // Required to override the method
        super.onResume();
        // Setup default Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Get username from Shared Preferences
        String username = sharedPreferences.getString("username", "user");

        TextView mainActivityHeading = findViewById(R.id.heading_main_activity);
        mainActivityHeading.setText(username + getResources().getString(R.string.heading_with_username_main_activity));
    }
}
