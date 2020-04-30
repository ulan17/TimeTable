package com.ulan.timetable.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ulan.timetable.R;
import com.ulan.timetable.fragments.SummarySettingsFragment;
import com.ulan.timetable.utils.PreferenceUtil;

public class SummarySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new SummarySettingsFragment())
                .commit();
    }
}
