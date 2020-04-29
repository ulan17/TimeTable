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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ulan.timetable.fragments.WeekdayFragment;
import com.ulan.timetable.model.Week;

import java.util.ArrayList;
import java.util.Calendar;

public class WeekUtils {
    @Nullable
    public static Week getNextWeek(@NonNull ArrayList<Week> weeks) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
        String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        if (hour.length() < 2)
            hour = "0" + hour;
        String minutes = "" + calendar.get(Calendar.MINUTE);
        if (minutes.length() < 2)
            minutes = "0" + minutes;
        String now = hour + ":" + minutes;

        for (int i = 0; i < weeks.size(); i++) {
            Week week = weeks.get(i);
            if ((now.compareToIgnoreCase(week.getFromTime()) >= 0 && now.compareToIgnoreCase(week.getToTime()) <= 0) || now.compareToIgnoreCase(week.getToTime()) <= 0) {
                return week;
            }
        }
        return null;
    }

    @NonNull
    public static ArrayList<Week> getAllWeeks(@NonNull DbHelper dbHelper) {
        return getWeeks(dbHelper, new String[]{WeekdayFragment.KEY_MONDAY_FRAGMENT,
                WeekdayFragment.KEY_TUESDAY_FRAGMENT,
                WeekdayFragment.KEY_WEDNESDAY_FRAGMENT,
                WeekdayFragment.KEY_THURSDAY_FRAGMENT,
                WeekdayFragment.KEY_FRIDAY_FRAGMENT,
                WeekdayFragment.KEY_SATURDAY_FRAGMENT,
                WeekdayFragment.KEY_SUNDAY_FRAGMENT});
    }

    @NonNull
    public static ArrayList<Week> getWeeks(@NonNull DbHelper dbHelper, @NonNull String[] keys) {
        ArrayList<Week> weeks = new ArrayList<>();
        for (String key : keys) {
            weeks.addAll(dbHelper.getWeek(key));
        }
        return weeks;
    }

    @NonNull
    public static String getNextOccurenceOfSubject(DbHelper dbHelper, String subject) {
/*        ArrayList<Week> weeks = new ArrayList<Week>();

        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                break;
            case Calendar.TUESDAY:
                break;
            case Calendar.WEDNESDAY:
                break;
            case Calendar.THURSDAY:
                break;
            case Calendar.FRIDAY:
                break;
            case Calendar.SATURDAY:
                break;
            case Calendar.SUNDAY:
                break;
        }

        String.format("%02d-%02d-%02d", year, month + 1, dayOfMonth)*/
        return ""; //TODO
    }
}
