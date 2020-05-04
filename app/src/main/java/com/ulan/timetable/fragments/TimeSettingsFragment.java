package com.ulan.timetable.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ulan.timetable.R;
import com.ulan.timetable.utils.PreferenceUtil;


public class TimeSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_time, rootKey);

        Preference myPref = findPreference("start_time");
        Preference finalMyPref = myPref;
        myPref.setOnPreferenceClickListener((Preference p) -> {
            int[] oldTimes = PreferenceUtil.getStartTime(getContext());
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    (view, hourOfDay, minute) -> {
                        PreferenceUtil.setStartTime(getContext(), hourOfDay, minute, 0);
                        finalMyPref.setSummary(hourOfDay + ":" + minute);
                    }, oldTimes[0], oldTimes[1], true);
            timePickerDialog.setTitle(R.string.start_of_school);
            timePickerDialog.show();
            return true;
        });
        int[] oldTimes = PreferenceUtil.getStartTime(getContext());
        myPref.setSummary(oldTimes[0] + ":" + oldTimes[1]);


        myPref = findPreference("set_period_length");
        Preference finalMyPref1 = myPref;
        myPref.setOnPreferenceClickListener((Preference p) -> {
            NumberPicker numberPicker = new NumberPicker(getContext());
            numberPicker.setMaxValue(180);
            numberPicker.setMinValue(1);
            numberPicker.setValue(PreferenceUtil.getPeriodLength(getContext()));
            new MaterialDialog.Builder(getContext())
                    .customView(numberPicker, false)
                    .positiveText(R.string.select)
                    .onPositive((d, w) -> {
                        int value = numberPicker.getValue();
                        PreferenceUtil.setPeriodLength(getContext(), value);
                        finalMyPref1.setSummary(value + " " + getString(R.string.minutes));
                    })
                    .show();
            return true;
        });
        myPref.setSummary(PreferenceUtil.getPeriodLength(getContext()) + " " + getString(R.string.minutes));
    }
}
