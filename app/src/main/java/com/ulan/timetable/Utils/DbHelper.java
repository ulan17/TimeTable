package com.ulan.timetable.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ulan.timetable.Model.Homework;
import com.ulan.timetable.Model.Note;
import com.ulan.timetable.Model.Week;

import java.util.ArrayList;

/**
 * Created by Ulan on 07.09.2018.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "timetabledb";
    private static final String TIMETABLE = "timetable";
    private static final String TIMETABLE_ID = "id";
    private static final String TIMETABLE_SUBJECT = "subject";
    private static final String TIMETABLE_FRAGMENT = "fragment";
    private static final String TIMETABLE_TEACHER = "teacher";
    private static final String TIMETABLE_ROOM = "room";
    private static final String TIMETABLE_FROM_TIME = "fromtime";
    private static final String TIMETABLE_TO_TIME = "totime";

    private static final String HOMEWORKS = "homeworks";
    private static final String HOMEWORKS_ID  = "id";
    private static final String HOMEWORKS_SUBJECT = "subject";
    private static final String HOMEWORKS_DESCRIPTION = "description";
    private static final String HOMEWORKS_DATE = "date";

    private static final String NOTES = "notes";
    private static final String NOTES_ID = "id";
    private static final String NOTES_TITLE = "title";
    private static final String NOTES_TEXT = "text";

    public DbHelper(Context context){
        super(context , DB_NAME, null, DB_VERSION);
    }

     public void onCreate(SQLiteDatabase db) {
        String CREATE_TIMETABLE = "CREATE TABLE " + TIMETABLE + "("
                + TIMETABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TIMETABLE_SUBJECT + " TEXT,"
                + TIMETABLE_FRAGMENT + " TEXT,"
                + TIMETABLE_TEACHER + " TEXT,"
                + TIMETABLE_ROOM + " TEXT,"
                + TIMETABLE_FROM_TIME + " TEXT,"
                + TIMETABLE_TO_TIME + " TEXT"+ ")";

        String CREATE_HOMEWORKS = "CREATE TABLE " + HOMEWORKS + "("
                + HOMEWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HOMEWORKS_SUBJECT + " TEXT,"
                + HOMEWORKS_DESCRIPTION + " TEXT,"
                + HOMEWORKS_DATE + " TEXT" + ")";

        String CREATE_NOTES = "CREATE TABLE " + NOTES + "("
                + NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTES_TITLE + " TEXT,"
                + NOTES_TEXT + " TEXT" + ")";

        db.execSQL(CREATE_TIMETABLE);
        db.execSQL(CREATE_HOMEWORKS);
        db.execSQL(CREATE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);

            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + HOMEWORKS);

            case 3:
                db.execSQL("DROP TABLE IF EXISTS " + NOTES);
                break;
        }
        onCreate(db);
    }

    // For Week fragments
    public void insertWeek(Week week){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMETABLE_SUBJECT, week.getSubject());
        contentValues.put(TIMETABLE_FRAGMENT, week.getFragment());
        contentValues.put(TIMETABLE_TEACHER, week.getTeacher());
        contentValues.put(TIMETABLE_ROOM, week.getRoom());
        contentValues.put(TIMETABLE_FROM_TIME, week.getFromTime());
        contentValues.put(TIMETABLE_TO_TIME, week.getToTime());
        db.insert(TIMETABLE,null, contentValues);
        db.update(TIMETABLE, contentValues, TIMETABLE_FRAGMENT, null);
        db.close();
    }

    public void deleteWeekById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TIMETABLE, TIMETABLE_ID + " = ? ", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateWeek(Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMETABLE_SUBJECT, week.getSubject());
        contentValues.put(TIMETABLE_TEACHER, week.getTeacher());
        contentValues.put(TIMETABLE_ROOM, week.getRoom());
        contentValues.put(TIMETABLE_FROM_TIME,week.getFromTime());
        contentValues.put(TIMETABLE_TO_TIME, week.getToTime());
        db.update(TIMETABLE, contentValues, TIMETABLE_ID + " = " + week.getId(), null);
        db.close();
    }

    public ArrayList<Week> getWeek(String fragment){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Week> weeklist = new ArrayList<>();
        Week week;
        Cursor cursor = db.rawQuery("SELECT * FROM "+TIMETABLE+" WHERE "+ TIMETABLE_FRAGMENT +" LIKE '"+fragment+"%'",null);
        while (cursor.moveToNext()){
            week = new Week();
            week.setId(cursor.getInt(cursor.getColumnIndex(TIMETABLE_ID)));
            week.setSubject(cursor.getString(cursor.getColumnIndex(TIMETABLE_SUBJECT)));
            week.setTeacher(cursor.getString(cursor.getColumnIndex(TIMETABLE_TEACHER)));
            week.setRoom(cursor.getString(cursor.getColumnIndex(TIMETABLE_ROOM)));
            week.setFromTime(cursor.getString(cursor.getColumnIndex(TIMETABLE_FROM_TIME)));
            week.setToTime(cursor.getString(cursor.getColumnIndex(TIMETABLE_TO_TIME)));
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
            homework.setId(cursor.getInt(cursor.getColumnIndex(HOMEWORKS_ID)));
            homework.setSubject(cursor.getString(cursor.getColumnIndex(HOMEWORKS_SUBJECT)));
            homework.setDescription(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DESCRIPTION)));
            homework.setDate(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DATE)));
            homelist.add(homework);
        }
        cursor.close();
        db.close();
        return  homelist;
    }

    //For Notes activity
    public void insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        db.insert(NOTES, null, contentValues);
        db.close();
    }

    public void updateNote(Note note)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        db.update(NOTES, contentValues, NOTES_ID + " = " + note.getId(), null);
        db.close();
    }

    public void deleteNoteById(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES, NOTES_ID + " =? ", new String[] {String.valueOf(note.getId())});
        db.close();
    }

    public ArrayList<Note> getNote() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> notelist = new ArrayList<>();
        Note note;
        Cursor cursor = db.rawQuery("SELECT * FROM " + NOTES, null);
        while (cursor.moveToNext()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex(NOTES_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndex(NOTES_TITLE)));
            note.setText(cursor.getString(cursor.getColumnIndex(NOTES_TEXT)));
            notelist.add(note);
        }
        cursor.close();
        db.close();
        return notelist;
    }
}
