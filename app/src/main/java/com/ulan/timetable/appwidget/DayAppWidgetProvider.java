package com.ulan.timetable.appwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.ulan.timetable.R;
import com.ulan.timetable.appwidget.Dao.AppWidgetDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * From https://github.com/SubhamTyagi/TimeTable
 */
public class DayAppWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_RESTORE = "com.ulan.timetable" + ".ACTION_RESTORE";
    private static final String ACTION_YESTERDAY = "com.ulan.timetable" + ".ACTION_YESTERDAY";
    private static final String ACTION_TOMORROW = "com.ulan.timetable" + ".ACTION_TOMORROW";
    private static final String ACTION_NEW_DAY = "com.ulan.timetable" + ".ACTION_NEW_DAY";

    private static final int ONE_DAY_MILLIS = 86400000;

    @Override
    public void onEnabled(Context context) {
        registerNewDayBroadcast(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if (isAlarmManagerNotSet(context)) {
            registerNewDayBroadcast(context);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d M E", Locale.getDefault());

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, DayAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            long currentTimeMillis = System.currentTimeMillis();
            AppWidgetDao.saveAppWidgetCurrentTime(appWidgetId, currentTimeMillis, context);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.day_appwidget);
            rv.setRemoteAdapter(R.id.lv_day_appwidget, intent);
            rv.setEmptyView(R.id.lv_day_appwidget, R.id.empty_view);
            rv.setTextViewText(R.id.tv_date, getDateText(appWidgetId, currentTimeMillis, simpleDateFormat, context));
            rv.setInt(R.id.fl_root, "setBackgroundColor", AppWidgetDao.getAppWidgetBackgroundColor(appWidgetId, Color.TRANSPARENT, context));

            rv.setOnClickPendingIntent(R.id.imgBtn_restore, makePendingIntent(context, appWidgetId, ACTION_RESTORE));
            rv.setOnClickPendingIntent(R.id.imgBtn_yesterday, makePendingIntent(context, appWidgetId, ACTION_YESTERDAY));
            rv.setOnClickPendingIntent(R.id.imgBtn_tomorrow, makePendingIntent(context, appWidgetId, ACTION_TOMORROW));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_day_appwidget);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    private static String getDateText(int appWidgetId, long currentTimeMillis, SimpleDateFormat simpleDateFormat, Context context) {
        String date = simpleDateFormat.format(currentTimeMillis);
        if (AppWidgetDao.getAppWidgetWeekStyle(appWidgetId, AppWidgetConstants.WEEK_STYLE_DISABLE, context) == AppWidgetConstants.WEEK_STYLE_ENABLE) {
            return date + " " + /*(CalendarUtil.getCurrentWeek(currentTimeMillis) + 1)*/ "week";
        } else {
            return date;
        }
    }

    public static void noticeAppWidgetViewDataChanger(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                DayAppWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_day_appwidget);

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.day_appwidget);
            views.setTextViewText(R.id.tv_date, getDateText(appWidgetId, System.currentTimeMillis(), new SimpleDateFormat("D M E", Locale.getDefault()), context));
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetDao.deleteAppWidget(appWidgetId, context);
        }
    }

    @Override
    public void onDisabled(Context context) {
        unregisterNewDayBroadcast(context);
        AppWidgetDao.clear(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    private PendingIntent makePendingIntent(Context context, int appWidgetId, String action) {
        Intent intent = new Intent(context, DayAppWidgetProvider.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void updateAppWidgetConfig(AppWidgetManager appWidgetManager, int appWidgetId, int backgroundColor, int timeStyle, int weekStyle, Context context) {
        AppWidgetDao.saveAppWidgetConfig(appWidgetId, backgroundColor, timeStyle, weekStyle, context);

        Intent intent = new Intent(context, DayAppWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.day_appwidget);
        views.setRemoteAdapter(R.id.lv_day_appwidget, intent);
        views.setEmptyView(R.id.lv_day_appwidget, R.id.empty_view);
        views.setInt(R.id.fl_root, "setBackgroundColor", backgroundColor);
        views.setTextViewText(R.id.tv_date, getDateText(appWidgetId, System.currentTimeMillis(), new SimpleDateFormat("d M E", Locale.getDefault()), context));
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (ACTION_NEW_DAY.equals(action)) {
            notifyUpdate(context);
            return;
        }

        if (ACTION_RESTORE.equals(action) || ACTION_YESTERDAY.equals(action) || ACTION_TOMORROW.equals(action)) {

            //TODO: date format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d M E", Locale.getDefault());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.day_appwidget);
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager
                    .INVALID_APPWIDGET_ID);

            long currentTime;
            long newTime;

            if (ACTION_RESTORE.equals(action)) {
                rv.setViewVisibility(R.id.imgBtn_restore, View.INVISIBLE);
                newTime = System.currentTimeMillis();
            } else if (ACTION_YESTERDAY.equals(action)) {
                rv.setViewVisibility(R.id.imgBtn_restore, View.VISIBLE);
                currentTime = AppWidgetDao.getAppWidgetCurrentTime(appWidgetId, System.currentTimeMillis(), context);
                newTime = currentTime - ONE_DAY_MILLIS;
            } else { //ACTION_TOMORROW
                rv.setViewVisibility(R.id.imgBtn_restore, View.VISIBLE);
                currentTime = AppWidgetDao.getAppWidgetCurrentTime(appWidgetId, System.currentTimeMillis(), context);
                newTime = currentTime + ONE_DAY_MILLIS;
            }

            AppWidgetDao.saveAppWidgetCurrentTime(appWidgetId, newTime, context);
            rv.setTextViewText(R.id.tv_date, getDateText(appWidgetId, newTime, simpleDateFormat, context));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_day_appwidget);
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, rv);
        }

        super.onReceive(context, intent);
    }

    public void notifyUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                DayAppWidgetProvider.class));
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void registerNewDayBroadcast(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, DayAppWidgetProvider.class);
        intent.setAction(ACTION_NEW_DAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar midnight = Calendar.getInstance(Locale.getDefault());
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 1); //
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), ONE_DAY_MILLIS, pendingIntent);
    }

    private void unregisterNewDayBroadcast(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, DayAppWidgetProvider.class);
        intent.setAction(ACTION_NEW_DAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private boolean isAlarmManagerNotSet(Context context) {
        Intent intent = new Intent(context, DayAppWidgetProvider.class);
        intent.setAction(ACTION_NEW_DAY);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) == null;
    }

}