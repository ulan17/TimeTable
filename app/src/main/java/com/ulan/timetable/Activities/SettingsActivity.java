package com.ulan.timetable.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ulan.timetable.Fragments.SettingsFragment;
import com.ulan.timetable.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String
            KEY_SEVEN_DAYS_SETTING = "sevendays";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
