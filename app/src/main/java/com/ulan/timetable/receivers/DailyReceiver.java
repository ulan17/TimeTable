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

package com.ulan.timetable.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.ulan.timetable.utils.NotificationUtil;
import com.ulan.timetable.utils.PreferenceUtil;

/**
 * Created by Ulan on 28.01.2019.
 */
public class DailyReceiver extends BroadcastReceiver {

    public static final int DailyReceiverID = 10000;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                if (PreferenceUtil.isAlarmOn(context)) {
                    int[] times = PreferenceUtil.getAlarmTime(context);
                    PreferenceUtil.setRepeatingAlarm(context, DailyReceiver.class, times[0], times[1], times[2], DailyReceiverID, AlarmManager.INTERVAL_DAY);
                } else
                    PreferenceUtil.cancelAlarm(context, DailyReceiver.class, DailyReceiverID);
                NotificationUtil.sendNotificationSummary(context, true);
                return;
            }
        }

        if (!PreferenceUtil.isAlarmOn(context)) {
            PreferenceUtil.cancelAlarm(context, DailyReceiver.class, DailyReceiverID);
        } else {
            NotificationUtil.sendNotificationSummary(context, true);
        }
    }

}