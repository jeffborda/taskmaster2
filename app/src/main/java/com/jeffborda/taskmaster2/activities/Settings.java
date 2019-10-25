package com.jeffborda.taskmaster2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jeffborda.taskmaster2.R;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button updateUsernameButton = findViewById(R.id.update_username_button);
        updateUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {

                // Hide keyboard on button tap
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                EditText usernameEditText = findViewById(R.id.username_field);
                if(TextUtils.isEmpty(usernameEditText.getText())) {
                    Log.i(TAG, "Username field empty, username not updated.");
                    usernameEditText.setError("Username cannot be blank");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_no_username),Toast.LENGTH_LONG).show();
                }
                else {
                    // Get the string to save
                    String username = usernameEditText.getText().toString();
                    // Setup SharedPreferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    // Setup SharedPreferences Editor
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Save the string
                    editor.putString("username", username);
                    // Apply the change (like a commit, but more efficient)
                    editor.apply();



                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_username_updated_success),Toast.LENGTH_LONG).show();
                    // Send back to MainActivity after updating username
                    Intent mainActivityIntent = new Intent(Settings.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                }

            }
        });
    }
}
