package com.ulan.timetable.appwidget.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * From https://github.com/SubhamTyagi/TimeTable
 */
public class AppWidgetDao extends BaseDao {

    private static final String TABLE_NAME = "app_widget";

    public static void saveAppWidgetConfig(int appWidgetId, int backgroundColor, int timeStyle, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);

        ContentValues values = new ContentValues(4);
        values.put("backgroundColor", backgroundColor);
        values.put("timeStyle", timeStyle);

        String whereClause = "appWidgetId = ?";
        String[] whereArgs = {String.valueOf(appWidgetId)};

        int number = update(db, TABLE_NAME, values, whereClause, whereArgs);

        if (number == 0) {

            values.put("appWidgetId", appWidgetId);
            insert(db, TABLE_NAME, values);
        }

        DBManager.close(db);
    }

    public static Map<String, Integer> getAppWidgetConfig(int appWidgetId, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        String selection = "appWidgetId = ?";
        String[] selectionArgs = {String.valueOf(appWidgetId)};
        String[] columns = {"backgroundColor", "timeStyle", "weekStyle"};
        Cursor cursor = queryComplex(db, TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            return null;
        }

        Map<String, Integer> configMap = null;

        if (cursor.moveToNext()) {// id只存在一个，所以不用while
            configMap = new HashMap<>();
            configMap.put("backgroundColor", cursor.getInt(cursor.getColumnIndex("backgroundColor")));
            configMap.put("timeStyle", cursor.getInt(cursor.getColumnIndex("timeStyle")));
            configMap.put("weekStyle", cursor.getInt(cursor.getColumnIndex("weekStyle")));
        }

        cursor.close();

        return configMap;
    }

    public static int getAppWidgetBackgroundColor(int appWidgetId, int defaultColor, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        String selection = "appWidgetId = ?";
        String[] selectionArgs = {String.valueOf(appWidgetId)};
        String[] columns = {"backgroundColor"};
        Cursor cursor = queryComplex(db, TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            return defaultColor;
        }

        int backgroundColorIndex = cursor.getColumnIndex("backgroundColor");
        int backgroundColor;

        if (cursor.moveToNext()) {// id只存在一个，所以不用while
            backgroundColor = cursor.getInt(backgroundColorIndex);
        } else {
            backgroundColor = defaultColor;
        }

        cursor.close();

        return backgroundColor;
    }

    public static int getAppWidgetTimeStyle(int appWidgetId, int defaultTimeStyle, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        String selection = "appWidgetId = ?";
        String[] selectionArgs = {String.valueOf(appWidgetId)};
        String[] columns = {"timeStyle"};
        Cursor cursor = queryComplex(db, TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            return defaultTimeStyle;
        }

        int timeStyleIndex = cursor.getColumnIndex("timeStyle");
        int timeStyle;

        if (cursor.moveToNext()) {// id只存在一个，所以不用while
            timeStyle = cursor.getInt(timeStyleIndex);
        } else {
            timeStyle = defaultTimeStyle;
        }

        cursor.close();

        return timeStyle;
    }

    public static void saveAppWidgetCurrentTime(int appWidgetId, long currentTime, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);

        ContentValues values = new ContentValues(2);
        values.put("currentTime", currentTime);

        String whereClause = "appWidgetId = ?";
        String[] whereArgs = {String.valueOf(appWidgetId)};

        int number = update(db, TABLE_NAME, values, whereClause, whereArgs);

        if (number == 0) {
            // 使用insertOrReplace会重置其他列的数据
            values.put("appWidgetId", appWidgetId);
            insert(db, TABLE_NAME, values);
        }

        DBManager.close(db);
    }

    public static long getAppWidgetCurrentTime(int appWidgetId, long defaultTime, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        String selection = "appWidgetId = ?";
        String[] selectionArgs = {String.valueOf(appWidgetId)};
        String[] columns = {"currentTime"};
        Cursor cursor = queryComplex(db, TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            return defaultTime;
        }

        int currentTimeIndex = cursor.getColumnIndex("currentTime");
        long currentTime = 0;

        if (cursor.moveToNext()) {// id只存在一个，所以不用while
            currentTime = cursor.getLong(currentTimeIndex);
        }

        if (currentTime == 0) {
            currentTime = defaultTime;
        }

        cursor.close();

        return currentTime;
    }

    public static void deleteAppWidget(int appWidgetId, Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        delete(db, TABLE_NAME, "appWidgetId = ?", new String[]{String.valueOf(appWidgetId)});
        DBManager.close(db);
    }

    public static void clear(Context context) {
        SQLiteDatabase db = DBManager.getDb(context);
        delete(db, TABLE_NAME, null, null);
        DBManager.close(db);
    }
}
