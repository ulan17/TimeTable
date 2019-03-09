package com.ulan.timetable.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.ulan.timetable.activities.MainActivity;
import com.ulan.timetable.R;

import java.util.Calendar;

/**
 * Created by Ulan on 28.01.2019.
 */
public class DailyReceiver extends BroadcastReceiver {

    Context context;
    DbHelper db;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String message ;

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        db = new DbHelper(context);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        message = getLessons(day);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context, "").setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message).setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        if (notificationManager != null) {
            notificationManager.notify(5, mNotifyBuilder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getLessons(int day) {
        StringBuilder lessons = new StringBuilder("");
        String currentDay = getCurrentDay(day);

        db.getWeek(currentDay).forEach(week -> {
            if(week != null) {
                lessons.append(week.getSubject()).append(" ")
                        .append(week.getFromTime())
                        .append(" - ")
                        .append(week.getToTime()).append(" ")
                        .append(week.getRoom())
                        .append("\n");
            }
        });

        return !lessons.toString().equals("") ? lessons.toString() : context.getString(R.string.do_not_have_lessons);
    }

    private String getCurrentDay(int day) {
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