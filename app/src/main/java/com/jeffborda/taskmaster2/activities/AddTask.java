package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jeffborda.taskmaster2.R;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }
}
