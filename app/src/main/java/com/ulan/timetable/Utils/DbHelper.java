package com.ulan.timetable.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ulan.timetable.Homework;
import com.ulan.timetable.Week;

import java.util.ArrayList;

/**
 * Created by Ulan on 07.09.2018.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "timetabledb";
    private static final String TIMETABLE = "timetable";
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_FRAGMENT = "fragment";
    private static final String KEY_TEACHER = "teacher";
    private static final String KEY_ROOM = "room";
    private static final String KEY_FROM_TIME = "fromtime";
    private static final String KEY_TO_TIME = "totime";

    private static final String HOMEWORKS = "homeworks";
    private static final String HOMEWORKS_ID  = "homeworksid";
    private static final String HOMEWORKS_SUBJECT = "homeworkssubject";
    private static final String HOMEWORKS_DESCRIPTION = "homeworksdescription";
    private static final String HOMEWORKS_DATE = "homeworksdate";

    public DbHelper(Context context){
        super(context , DB_NAME, null, DB_VERSION);

    }

     public void onCreate(SQLiteDatabase db) {
        String CREATE_TIMETABLE = "CREATE TABLE " + TIMETABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SUBJECT + " TEXT,"
                + KEY_FRAGMENT + " TEXT,"
                + KEY_TEACHER + " TEXT,"
                + KEY_ROOM + " TEXT,"
                + KEY_FROM_TIME + " TEXT,"
                + KEY_TO_TIME + " TEXT"+ ")";

        String CREATE_HOMEWORK = "CREATE TABLE " + HOMEWORKS + "("
                + HOMEWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HOMEWORKS_SUBJECT + " TEXT,"
                + HOMEWORKS_DESCRIPTION + " TEXT,"
                + HOMEWORKS_DATE + " TEXT" + ")";

        db.execSQL(CREATE_TIMETABLE);
        db.execSQL(CREATE_HOMEWORK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);

            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + HOMEWORKS);
                break;
        }
        onCreate(db);
    }

    // For Week fragments
    public void insertWeekDetails(Week week){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SUBJECT, week.getSubject());
        contentValues.put(KEY_FRAGMENT, week.getFragment());
        contentValues.put(KEY_TEACHER, week.getTeacher());
        contentValues.put(KEY_ROOM, week.getRoom());
        contentValues.put(KEY_FROM_TIME, week.getFromTime());
        contentValues.put(KEY_TO_TIME, week.getToTime());
        db.insert(TIMETABLE,null, contentValues);
        db.update(TIMETABLE, contentValues, KEY_FRAGMENT, null);
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
        contentValues.put(KEY_TEACHER, week.getTeacher());
        contentValues.put(KEY_ROOM,week.getRoom());
        contentValues.put(KEY_FROM_TIME,week.getFromTime());
        contentValues.put(KEY_TO_TIME, week.getToTime());
        db.update(TIMETABLE, contentValues, KEY_ID + " = " + week.getId(), null);
        db.close();
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
            week.setTeacher(cursor.getString(cursor.getColumnIndex(KEY_TEACHER)));
            week.setRoom(cursor.getString(cursor.getColumnIndex(KEY_ROOM)));
            week.setFromTime(cursor.getString(cursor.getColumnIndex(KEY_FROM_TIME)));
            week.setToTime(cursor.getString(cursor.getColumnIndex(KEY_TO_TIME)));
            weeklist.add(week);
        }
        return  weeklist;
    }

    // For Homework activity
    public void insertHomework(Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        db.insert(HOMEWORKS,null, contentValues);
        db.close();
    }

    public void updateHomework(Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        db.update(HOMEWORKS, contentValues, HOMEWORKS_ID + " = " + homework.getId(), null);
        db.close();
    }

    public void deleteHomeworkById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HOMEWORKS,HOMEWORKS_ID + " = ? ", new String[]{String.valueOf(id)});
        db.close();
    }


    public ArrayList<Homework> getHomework() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Homework> homelist = new ArrayList<>();
        Homework homework;
        Cursor cursor = db.rawQuery("SELECT * FROM "+ HOMEWORKS,null);
        while (cursor.moveToNext()){
            homework = new Homework();
            homework.setId(cursor.getInt(0));
            homework.setSubject(cursor.getString(cursor.getColumnIndex(HOMEWORKS_SUBJECT)));
            homework.setDescription(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DESCRIPTION)));
            homework.setDate(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DATE)));
            homelist.add(homework);
        }
        cursor.close();
        db.close();
        return  homelist;
    }
}
