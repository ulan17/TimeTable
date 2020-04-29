/*
 * Copyright (c) 2020 Felix Hollederer
 *     This file is part of GymWenApp.
 *
 *     GymWenApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GymWenApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GymWenApp.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ulan.timetable.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ulan.timetable.R;
import com.ulan.timetable.receivers.DoNotDisturbReceiversKt;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class PreferenceUtil {

    private static boolean getBooleanSettings(Context context, String key, boolean defaulValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaulValue);
    }

    public static boolean isNotification(Context context) {
        return getBooleanSettings(context, "timetableNotif", true);
    }

    public static void setAlarmTime(Context context, @NonNull int... times) {
        if (times.length != 3) {
            if (times.length > 0 && times[0] == 0) {
                setAlarm(context, false);
            } else {
                System.out.println("wrong parameters");
            }
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        setAlarm(context, true);
        editor.putInt("timetable_Alarm_hour", times[0]);
        editor.putInt("timetable_Alarm_minute", times[1]);
        editor.putInt("timetable_Alarm_second", times[2]);
        editor.commit();
    }

    @NonNull
    public static int[] getAlarmTime(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return new int[]{sharedPref.getInt("timetable_Alarm_hour", 7), sharedPref.getInt("timetable_Alarm_minute", 55), sharedPref.getInt("timetable_Alarm_second", 0)};
    }

    private static void setAlarm(@NonNull Context context, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("timetable_alarm", value);
        editor.commit();
    }


    public static boolean isAlarmOn(@NonNull Context context) {
        return getBooleanSettings(context, "timetable_alarm", false);
    }

    public static boolean doNotDisturbDontAskAgain(Context context) {
        return getBooleanSettings(context, "do_not_disturb_dont_ask", false);
    }

    public static void setDoNotDisturbDontAskAgain(@NonNull Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("do_not_disturb_dont_ask", value).apply();
    }

    public static boolean isAutomaticDoNotDisturb(Context context) {
        return getBooleanSettings(context, "automatic_do_not_disturb", true);
    }

    public static void setDoNotDisturb(@NonNull Activity activity, boolean dontAskAgain) {
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the notification policy access has been granted for the app.
            if (!notificationManager.isNotificationPolicyAccessGranted() && !dontAskAgain) {
                Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_do_not_disturb_on_black_24dp);
                new MaterialDialog.Builder(activity)
                        .title(R.string.permission_required)
                        .content(R.string.do_not_disturb_permission_desc)
                        .onPositive((dialog, which) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            activity.startActivity(intent);
                        })
                        .positiveText(R.string.permission_ok_button)
                        .negativeText(R.string.permission_cancel_button)
                        .onNegative(((dialog, which) -> dialog.dismiss()))
                        .icon(drawable)
                        .onNeutral(((dialog, which) -> setDoNotDisturbDontAskAgain(activity, true)))
                        .neutralText(R.string.dont_show_again)
                        .show();
            } else {
                DoNotDisturbReceiversKt.setDoNotDisturbReceivers(activity);
            }
        }
    }

    public static boolean isDoNotDisturbTurnOff(Context context) {
        return getBooleanSettings(context, "do_not_disturb_turn_off", false);
    }


    public static boolean isAlwaysNotification() {
        return false;
    }


    public static void setOneTimeAlarm(@NonNull Context context, @NonNull Class<?> cls, int hour, int min, int second, int id) {
        // cancel already scheduled reminders
        cancelAlarm(context, cls, id);

        Calendar currentCalendar = Calendar.getInstance();

        Calendar customCalendar = Calendar.getInstance();
        customCalendar.setTimeInMillis(System.currentTimeMillis());
        customCalendar.set(Calendar.HOUR_OF_DAY, hour);
        customCalendar.set(Calendar.MINUTE, min);
        customCalendar.set(Calendar.SECOND, second);

        if (customCalendar.before(currentCalendar))
            customCalendar.add(Calendar.DATE, 1);

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager == null)
            return;

        long startTime = customCalendar.getTimeInMillis();
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                if (System.currentTimeMillis() < startTime)
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
            } else {
                if (System.currentTimeMillis() < startTime)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
            }
        } else {
            if (System.currentTimeMillis() < startTime)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
    }

    public static void setRepeatingAlarm(@NonNull Context context, @NonNull Class<?> cls, int hour, int min, int second, int id, long interval) {
        // cancel already scheduled reminders
        cancelAlarm(context, cls, id);

        Calendar currentCalendar = Calendar.getInstance();

        Calendar customCalendar = Calendar.getInstance();
        customCalendar.setTimeInMillis(System.currentTimeMillis());
        customCalendar.set(Calendar.HOUR_OF_DAY, hour);
        customCalendar.set(Calendar.MINUTE, min);
        customCalendar.set(Calendar.SECOND, second);

        if (customCalendar.before(currentCalendar))
            customCalendar.add(Calendar.DATE, 1);

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager == null)
            return;

        long startTime = customCalendar.getTimeInMillis();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
    }

    public static void cancelAlarm(@NonNull Context context, @NonNull Class<?> cls, int id) {
        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
