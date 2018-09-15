package com.ulan.timetable.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ulan.timetable.Week;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ulan on 07.09.2018.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "timetabledb";
    private static final String TIMETABLE = "timetable";
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_FRAGMENT = "fragment";
    private static final String KEY_TIME = "room";
    private static final String KEY_ROOM = "time";

    public DbHelper(Context context){
        super(context , DB_NAME, null, DB_VERSION);

    }

     public void onCreate(SQLiteDatabase db) {
        String CREATE_TB = "CREATE TABLE " + TIMETABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SUBJECT + " TEXT,"
                + KEY_FRAGMENT + " TEXT,"
                + KEY_ROOM + " TEXT,"
                + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_TB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);
        onCreate(db);
    }

    public void insertWeekDetails(Week week){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_SUBJECT, week.getSubject());
        cValues.put(KEY_FRAGMENT, week.getFragment());
        cValues.put(KEY_ROOM, week.getRoom());
        cValues.put(KEY_TIME, week.getTime());
        db.insert(TIMETABLE,null, cValues);
        db.update(TIMETABLE, cValues, KEY_FRAGMENT, null);
        db.close();
    }

    public void deleteDataById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TIMETABLE,KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateData(Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SUBJECT,week.getSubject());
        contentValues.put(KEY_FRAGMENT, week.getFragment());
        contentValues.put(KEY_ROOM,week.getRoom());
        contentValues.put(KEY_TIME,week.getTime());
        db.update(TIMETABLE, contentValues, KEY_FRAGMENT, null);
        db.close();
    }

    public ArrayList<HashMap<String, String>> GetUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, category FROM "+ TIMETABLE;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_FRAGMENT)));
            user.put("category",cursor.getString(cursor.getColumnIndex(KEY_FRAGMENT)));
            userList.add(user);
        }
        return  userList;
    }

    public ArrayList<Week> getData(String fragment){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Week> weeklist = new ArrayList<>();
        Week week;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TIMETABLE+" WHERE "+KEY_FRAGMENT+" LIKE '"+fragment+"%'",null);
        while (cursor.moveToNext()){
            week = new Week();
            week.setId(cursor.getInt(0));
            week.setSubject(cursor.getString(cursor.getColumnIndex(KEY_SUBJECT)));
            week.setRoom(cursor.getString(cursor.getColumnIndex(KEY_ROOM)));
            week.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
            weeklist.add(week);
        }
        return  weeklist;
    }

}
