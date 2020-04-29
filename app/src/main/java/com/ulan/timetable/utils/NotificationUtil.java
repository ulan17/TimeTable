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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.TaskStackBuilder;
import androidx.core.graphics.drawable.DrawableKt;
import androidx.core.graphics.drawable.IconCompat;

import com.github.stephenvinouze.shapetextdrawable.ShapeForm;
import com.github.stephenvinouze.shapetextdrawable.ShapeTextDrawable;
import com.ulan.timetable.R;
import com.ulan.timetable.activities.MainActivity;
import com.ulan.timetable.model.Week;
import com.ulan.timetable.receivers.NotificationDismissButtonReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class NotificationUtil {
    private static final int NOTIFICATION_SUMMARY_ID = 9090;
    private static final int NOTIFICATION_NEXT_WEEK_ID = 3030;
    private static final String CHANNEL_ID = "timetable_notification";

    public static void sendNotificationSummary(@NonNull Context context, boolean alert) {
        new Thread(() -> {
            DbHelper db = new DbHelper(context);
            String lessons = getLessons(db.getWeek(getCurrentDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))), context);
            if (lessons == null)
                return;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                    .setContentTitle(context.getString(R.string.timetable_notification_summary_title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(lessons));

            sendNotification(context, alert, builder, NOTIFICATION_SUMMARY_ID);
        }).start();
    }

    public static void sendNotificationCurrentLesson(@NonNull Context context, boolean alert) {
        new Thread(() -> {
            DbHelper db = new DbHelper(context);
            ArrayList<Week> weeks = db.getWeek(getCurrentDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
            Week nextWeek = WeekUtils.getNextWeek(weeks);
            if (nextWeek == null)
                return;
            weeks = new ArrayList<>();
            weeks.add(nextWeek);

            StringBuilder lesson = new StringBuilder()
                    .append(context.getString(R.string.time_from).substring(0, 1).toUpperCase())
                    .append(context.getString(R.string.time_from).substring(1))
                    .append(" ")
                    .append(nextWeek.getFromTime())
                    .append(" - ")
                    .append(nextWeek.getToTime())
                    .append(" ")
                    .append(context.getString(R.string.share_msg_in_room))
                    .append(" ")
                    .append(nextWeek.getRoom());


            StringBuilder name = new StringBuilder()
                    .append(nextWeek.getSubject())
                    .append(" ")
                    .append(context.getString(R.string.with_teacher))
                    .append(" ")
                    .append(nextWeek.getTeacher());


            NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(new Person.Builder().setName("me").build());
            style.setConversationTitle(context.getString(R.string.timetable_notification_next_week_title));
            int color = nextWeek.getColor();
            int textColor = ColorPalette.pickTextColorBasedOnBgColorSimple(nextWeek.getColor(), Color.WHITE, Color.BLACK);
            int textSize = context.getResources().getInteger(R.integer.notification_max_text_size) - context.getResources().getInteger(R.integer.notification_text_size_timetable_factor) * nextWeek.getRoom().length();
            if (textSize < context.getResources().getInteger(R.integer.notification_min_text_size))
                textSize = context.getResources().getInteger(R.integer.notification_min_text_size);
            Drawable drawable = new ShapeTextDrawable(ShapeForm.ROUND, color, 10f, nextWeek.getRoom(), textColor, true, Typeface.create("sans-serif-light", Typeface.NORMAL), textSize, Color.TRANSPARENT, 0);
            Person person = new Person.Builder().setName(name).setIcon(IconCompat.createWithBitmap(DrawableKt.toBitmap(drawable, context.getResources().getInteger(R.integer.notification_bitmap_size), context.getResources().getInteger(R.integer.notification_bitmap_size), null))).build();
            style.addMessage(new NotificationCompat.MessagingStyle.Message(lesson, 0, person));

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setStyle(style)
                    .setSmallIcon(R.drawable.ic_assignment_next_black_24dp)
                    .setColor(color);


            sendNotification(context, alert, builder, NOTIFICATION_NEXT_WEEK_ID);
        }).start();
    }

    private static void sendNotification(@NonNull Context context, boolean alert, @Nullable NotificationCompat.Builder notificationBuilder, int id) {
        if (notificationBuilder == null || !PreferenceUtil.isNotification(context) || Build.VERSION.SDK_INT < 21)
            return;


        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel(context);
        NotificationCompat.Builder mNotifyBuilder = notificationBuilder
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setWhen(when)
                .setPriority(alert ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(!alert)
                .setContentIntent(pendingIntent);


        if (PreferenceUtil.isAlwaysNotification(context)) {
            //Dismiss button intent
            Intent buttonIntent = new Intent(context, NotificationDismissButtonReceiver.class);
            buttonIntent.setAction("com.asdoi.gymwen.receivers.NotificationDismissButtonReceiver");
            buttonIntent.putExtra(NotificationDismissButtonReceiver.EXTRA_NOTIFICATION_ID, id);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotifyBuilder.setOngoing(true);
            mNotifyBuilder.addAction(R.drawable.ic_close_black_24dp, context.getString(R.string.notif_dismiss), btPendingIntent);
        }

        if (notificationManager != null) {
            notificationManager.notify(id, mNotifyBuilder.build());
        }
    }

    private static void createNotificationChannel(@NonNull Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.timetable_channel), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.timetable_channel_desc));
            channel.enableLights(false);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    private static String getLessons(@NonNull ArrayList<Week> weeks, @NonNull Context context) {
        StringBuilder lessons = new StringBuilder();
        for (Week week : weeks) {
            if (week != null) {
                lessons.append(week.getSubject())
                        .append(" ")
                        .append(context.getString(R.string.time_from))
                        .append(" ")
                        .append(week.getFromTime())
                        .append(" - ")
                        .append(week.getToTime())
                        .append(" ")
                        .append(context.getString(R.string.with_teacher))
                        .append(" ")
                        .append(week.getTeacher())
                        .append(" ")
                        .append(context.getString(R.string.share_msg_in_room))
                        .append(" ")
                        .append(week.getRoom())
                        .append("\n");
            }
        }

        return !lessons.toString().equals("") ? lessons.toString().substring(0, lessons.toString().length() - 1) : null;
    }

    @Nullable
    public static String getCurrentDay(int day) {
        String currentDay = null;
        switch (day) {
            case 1:
                currentDay = "Sunday";
                break;
            case 2:
                currentDay = "Monday";
                break;
            case 3:
                currentDay = "Tuesday";
                break;
            case 4:
                currentDay = "Wednesday";
                break;
            case 5:
                currentDay = "Thursday";
                break;
            case 6:
                currentDay = "Friday";
                break;
            case 7:
                currentDay = "Saturday";
                break;
        }
        return currentDay;
    }
}
