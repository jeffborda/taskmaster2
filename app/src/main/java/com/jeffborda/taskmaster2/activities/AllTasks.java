package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jeffborda.taskmaster2.R;

public class AllTasks extends AppCompatActivity {

    private static final String TAG = "jtb.AllTasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
    }
}
