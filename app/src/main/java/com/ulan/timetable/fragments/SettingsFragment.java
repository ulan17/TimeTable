package com.ulan.timetable.fragments;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

import com.ulan.timetable.R;
import com.ulan.timetable.receivers.DailyReceiver;
import com.ulan.timetable.utils.PreferenceUtil;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        tintIcons(getPreferenceScreen(), PreferenceUtil.getTextColorPrimary(getContext()));

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
        int[] oldTimes = PreferenceUtil.getAlarmTime(getContext());
        myPref.setSummary(oldTimes[0] + ":" + oldTimes[1]);

        setTurnOff();
        myPref = findPreference("automatic_do_not_disturb");
        myPref.setOnPreferenceClickListener((Preference p) -> {
            PreferenceUtil.setDoNotDisturb(getActivity(), false);
            setTurnOff();
            return true;
        });

        ListPreference mp = findPreference("theme");
        mp.setOnPreferenceChangeListener((preference, newValue) -> {
            mp.setValue(newValue + "");
            getActivity().recreate();
            return false;
        });
        mp.setSummary(getThemeName());
    }

    private String getThemeName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String selectedTheme = sharedPreferences.getString("theme", "switch");
        String[] values = getResources().getStringArray(R.array.theme_array_values);

        String[] names = getResources().getStringArray(R.array.theme_array);

        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(selectedTheme)) {
                return names[i];
            }
        }

        return "";
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

    private static void tintIcons(Preference preference, int color) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup group = ((PreferenceGroup) preference);
            for (int i = 0; i < group.getPreferenceCount(); i++) {
                tintIcons(group.getPreference(i), color);
            }
        } else {
            Drawable icon = preference.getIcon();
            if (icon != null) {
                DrawableCompat.setTint(icon, color);
            }
        }
    }
}
