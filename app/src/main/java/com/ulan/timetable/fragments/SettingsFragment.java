package com.ulan.timetable.fragments;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ulan.timetable.R;
import com.ulan.timetable.receivers.DailyReceiver;
import com.ulan.timetable.utils.PreferenceUtil;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference cyanea = findPreference("cyanea");
        cyanea.setOnPreferenceClickListener((Preference p) -> {
//            startActivity(new Intent(getActivity(), CyaneaSettingsActivity.class));
            return true;
        });

        setNotif();

        Preference myPref = findPreference("timetableNotif");
        myPref.setOnPreferenceClickListener((Preference preference) -> {
            setNotif();
            return true;
        });

        myPref = findPreference("timetable_alarm");
        myPref.setOnPreferenceClickListener((Preference p) -> {
            int[] oldTimes = PreferenceUtil.getAlarmTime(getContext());
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    (view, hourOfDay, minute) -> {
                        PreferenceUtil.setAlarmTime(getContext(), hourOfDay, minute, 0);
                        PreferenceUtil.setRepeatingAlarm(getContext(), DailyReceiver.class, hourOfDay, minute, 0, DailyReceiver.DailyReceiverID, AlarmManager.INTERVAL_DAY);
                    }, oldTimes[0], oldTimes[1], true);
            timePickerDialog.setTitle(R.string.choose_time);
            timePickerDialog.show();
            return true;
        });

        setTurnOff();
        myPref = findPreference("automatic_do_not_disturb");
        myPref.setOnPreferenceClickListener((Preference p) -> {
            PreferenceUtil.setDoNotDisturb(getActivity(), false);
            setTurnOff();
            return true;
        });
    }

    private void setNotif() {
        boolean show = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("timetableNotif", true);
        findPreference("alwaysNotification").setVisible(show);
        findPreference("timetable_alarm").setVisible(show);
    }

    private void setTurnOff() {
        boolean show = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("automatic_do_not_disturb", true);
        findPreference("do_not_disturb_turn_off").setVisible(show);
    }
}
