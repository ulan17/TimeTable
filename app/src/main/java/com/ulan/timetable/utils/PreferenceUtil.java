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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ulan.timetable.R;
import com.ulan.timetable.activities.SettingsActivity;
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
        editor.putInt("Alarm_hour", times[0]);
        editor.putInt("Alarm_minute", times[1]);
        editor.putInt("Alarm_second", times[2]);
        editor.commit();
    }

    @NonNull
    public static int[] getAlarmTime(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return new int[]{sharedPref.getInt("Alarm_hour", 7), sharedPref.getInt("Alarm_minute", 55), sharedPref.getInt("Alarm_second", 0)};
    }

    private static void setAlarm(@NonNull Context context, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("alarm", value);
        editor.commit();
    }


    public static boolean isAlarmOn(@NonNull Context context) {
        return getBooleanSettings(context, "alarm", false);
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


    public static boolean isAlwaysNotification(Context context) {
        return getBooleanSettings(context, "alwaysNotification", false);
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


    @StyleRes
    public static int getGeneralTheme(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return getThemeResFromPrefValue(sharedPref.getString("theme", "switch"), context);
    }

    @StyleRes
    private static int getThemeResFromPrefValue(@NonNull String themePrefValue, Context context) {
        switch (themePrefValue) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return R.style.AppTheme_Dark;
            case "black":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return R.style.AppTheme_Black;
            case "switch":
                int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        return R.style.AppTheme_Dark;
                    default:
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        return R.style.AppTheme_Light;
                }
            case "light":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return R.style.AppTheme_Light;
        }
    }

    @StyleRes
    public static int getGeneralThemeNoActionBar(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return getThemeResFromPrefValueNoActionBar(sharedPref.getString("theme", "switch"), context);
    }

    @StyleRes
    private static int getThemeResFromPrefValueNoActionBar(@NonNull String themePrefValue, Context context) {
        switch (themePrefValue) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return R.style.AppTheme_Dark_NoActionBar;
            case "black":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return R.style.AppTheme_Black_NoActionBar;
            case "switch":
                int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        return R.style.AppTheme_Dark_NoActionBar;
                    default:
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        return R.style.AppTheme_Light_NoActionBar;
                }
            case "light":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return R.style.AppTheme_Light_NoActionBar;
        }
    }

    public static boolean isDark(Context context) {
        int theme = getGeneralTheme(context);
        switch (theme) {
            case R.style.AppTheme_Dark:
            case R.style.AppTheme_Black:
                return true;
            case R.style.AppTheme_Light:
            default:
                return false;
        }
    }

    public static int getTextColorPrimary(@NonNull Context context) {
        return getThemeColor(android.R.attr.textColorPrimary, context);
    }

    public static int getPrimaryColor(@NonNull Context context) {
        return getThemeColor(R.attr.colorPrimary, context);
    }


    private static int getThemeColor(int themeAttributeId, @NonNull Context context) {
        try {
            TypedValue outValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            boolean wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true);
            if (wasResolved) {
                return ContextCompat.getColor(context, outValue.resourceId);
            } else {
                // fallback colour handling
                return Color.BLACK;
            }
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static boolean isSevenDays(Context context) {
        return getBooleanSettings(context, SettingsActivity.KEY_SEVEN_DAYS_SETTING, false);
    }

    public static boolean isSummaryLibrary1(Context context) {
        return getBooleanSettings(context, "summary_lib", true);
    }

    public static void setSummaryLibrary(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("summary_lib", value).commit();
    }

    public static void setStartTime(Context context, @NonNull int... times) {
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
        editor.putInt("start_hour", times[0]);
        editor.putInt("start_minute", times[1]);
        editor.putInt("start_second", times[2]);
        editor.commit();
    }

    @NonNull
    public static int[] getStartTime(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return new int[]{sharedPref.getInt("start_hour", 8), sharedPref.getInt("start_minute", 0), sharedPref.getInt("start_second", 0)};
    }

    public static void setPeriodLength(Context context, int length) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("period_length", length).apply();
    }

    public static int getPeriodLength(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("period_length", 60);
    }
}
