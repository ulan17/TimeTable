package com.ulan.timetable.appwidget.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * From https://github.com/SubhamTyagi/TimeTable
 */
class DBManager {

    private static AtomicInteger sOpenCounter = new AtomicInteger();

    static synchronized SQLiteDatabase getDb(Context context) {
        sOpenCounter.incrementAndGet();
        return new DataBaseHelper(context).getWritableDatabase();
    }


    static synchronized void close(SQLiteDatabase database) {
        if (sOpenCounter.decrementAndGet() == 0) {
            if (database != null) {
                database.close();
            }
        }
    }
}
