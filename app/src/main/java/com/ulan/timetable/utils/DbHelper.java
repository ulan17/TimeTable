package com.ulan.timetable.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ulan.timetable.model.Exam;
import com.ulan.timetable.model.Homework;
import com.ulan.timetable.model.Note;
import com.ulan.timetable.model.Teacher;
import com.ulan.timetable.model.Week;

import java.util.ArrayList;

/**
 * Created by Ulan on 07.09.2018.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 6;
    private static final String DB_NAME = "timetabledb";
    private static final String TIMETABLE = "timetable";
    private static final String WEEK_ID = "id";
    private static final String WEEK_SUBJECT = "subject";
    private static final String WEEK_FRAGMENT = "fragment";
    private static final String WEEK_TEACHER = "teacher";
    private static final String WEEK_ROOM = "room";
    private static final String WEEK_FROM_TIME = "fromtime";
    private static final String WEEK_TO_TIME = "totime";
    private static final String WEEK_COLOR = "color";

    private static final String HOMEWORKS = "homeworks";
    private static final String HOMEWORKS_ID  = "id";
    private static final String HOMEWORKS_SUBJECT = "subject";
    private static final String HOMEWORKS_DESCRIPTION = "description";
    private static final String HOMEWORKS_DATE = "date";
    private static final String HOMEWORKS_COLOR = "color";

    private static final String NOTES = "notes";
    private static final String NOTES_ID = "id";
    private static final String NOTES_TITLE = "title";
    private static final String NOTES_TEXT = "text";
    private static final String NOTES_COLOR = "color";

    private static final String TEACHERS = "teachers";
    private static final String TEACHERS_ID = "id";
    private static final String TEACHERS_NAME = "name";
    private static final String TEACHERS_POST = "post";
    private static final String TEACHERS_PHONE_NUMBER = "phonenumber";
    private static final String TEACHERS_EMAIL = "email";
    private static final String TEACHERS_COLOR = "color";

    private static final String EXAMS = "exams";
    private static final String EXAMS_ID = "id";
    private static final String EXAMS_SUBJECT = "subject";
    private static final String EXAMS_TEACHER = "teacher";
    private static final String EXAMS_ROOM = "room";
    private static final String EXAMS_DATE = "date";
    private static final String EXAMS_TIME = "time";
    private static final String EXAMS_COLOR = "color";


    public DbHelper(Context context){
        super(context , DB_NAME, null, DB_VERSION);
    }

     public void onCreate(SQLiteDatabase db) {
        String CREATE_TIMETABLE = "CREATE TABLE " + TIMETABLE + "("
                + WEEK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WEEK_SUBJECT + " TEXT,"
                + WEEK_FRAGMENT + " TEXT,"
                + WEEK_TEACHER + " TEXT,"
                + WEEK_ROOM + " TEXT,"
                + WEEK_FROM_TIME + " TEXT,"
                + WEEK_TO_TIME + " TEXT,"
                + WEEK_COLOR + " INTEGER" +  ")";

        String CREATE_HOMEWORKS = "CREATE TABLE " + HOMEWORKS + "("
                + HOMEWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HOMEWORKS_SUBJECT + " TEXT,"
                + HOMEWORKS_DESCRIPTION + " TEXT,"
                + HOMEWORKS_DATE + " TEXT,"
                + HOMEWORKS_COLOR + " INTEGER" + ")";

        String CREATE_NOTES = "CREATE TABLE " + NOTES + "("
                + NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTES_TITLE + " TEXT,"
                + NOTES_TEXT + " TEXT,"
                + NOTES_COLOR + " INTEGER" + ")";

        String CREATE_TEACHERS = "CREATE TABLE " + TEACHERS + "("
                + TEACHERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TEACHERS_NAME + " TEXT,"
                + TEACHERS_POST + " TEXT,"
                + TEACHERS_PHONE_NUMBER + " TEXT,"
                + TEACHERS_EMAIL + " TEXT,"
                + TEACHERS_COLOR + " INTEGER" + ")";

        String CREATE_EXAMS = "CREATE TABLE " + EXAMS + "("
                + EXAMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXAMS_SUBJECT + " TEXT,"
                + EXAMS_TEACHER + " TEXT,"
                + EXAMS_ROOM + " TEXT,"
                + EXAMS_DATE + " TEXT,"
                + EXAMS_TIME + " TEXT,"
                + EXAMS_COLOR + " INTEGER" + ")";

        db.execSQL(CREATE_TIMETABLE);
        db.execSQL(CREATE_HOMEWORKS);
        db.execSQL(CREATE_NOTES);
        db.execSQL(CREATE_TEACHERS);
        db.execSQL(CREATE_EXAMS);
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

            case 4:
                db.execSQL("DROP TABLE IF EXISTS " + TEACHERS);

            case 5:
                db.execSQL("DROP TABLE IF EXISTS " + EXAMS);
                break;
        }
        onCreate(db);
    }

    /**
     * Methods for Week fragments
     **/
    public void insertWeek(Week week){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEEK_SUBJECT, week.getSubject());
        contentValues.put(WEEK_FRAGMENT, week.getFragment());
        contentValues.put(WEEK_TEACHER, week.getTeacher());
        contentValues.put(WEEK_ROOM, week.getRoom());
        contentValues.put(WEEK_FROM_TIME, week.getFromTime());
        contentValues.put(WEEK_TO_TIME, week.getToTime());
        contentValues.put(WEEK_COLOR, week.getColor());
        db.insert(TIMETABLE,null, contentValues);
        db.update(TIMETABLE, contentValues, WEEK_FRAGMENT, null);
        db.close();
    }

    public void deleteWeekById(Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TIMETABLE, WEEK_ID + " = ? ", new String[]{String.valueOf(week.getId())});
        db.close();
    }

    public void updateWeek(Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEEK_SUBJECT, week.getSubject());
        contentValues.put(WEEK_TEACHER, week.getTeacher());
        contentValues.put(WEEK_ROOM, week.getRoom());
        contentValues.put(WEEK_FROM_TIME,week.getFromTime());
        contentValues.put(WEEK_TO_TIME, week.getToTime());
        contentValues.put(WEEK_COLOR, week.getColor());
        db.update(TIMETABLE, contentValues, WEEK_ID + " = " + week.getId(), null);
        db.close();
    }

    public ArrayList<Week> getWeek(String fragment){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Week> weeklist = new ArrayList<>();
        Week week;
        Cursor cursor = db.rawQuery("SELECT * FROM ( SELECT * FROM "+TIMETABLE+" ORDER BY " + WEEK_FROM_TIME + " ) WHERE "+ WEEK_FRAGMENT +" LIKE '"+fragment+"%'",null);
        while (cursor.moveToNext()){
            week = new Week();
            week.setId(cursor.getInt(cursor.getColumnIndex(WEEK_ID)));
            week.setSubject(cursor.getString(cursor.getColumnIndex(WEEK_SUBJECT)));
            week.setTeacher(cursor.getString(cursor.getColumnIndex(WEEK_TEACHER)));
            week.setRoom(cursor.getString(cursor.getColumnIndex(WEEK_ROOM)));
            week.setFromTime(cursor.getString(cursor.getColumnIndex(WEEK_FROM_TIME)));
            week.setToTime(cursor.getString(cursor.getColumnIndex(WEEK_TO_TIME)));
            week.setColor(cursor.getInt(cursor.getColumnIndex(WEEK_COLOR)));
            weeklist.add(week);
        }
        return  weeklist;
    }

    /**
     * Methods for Homeworks activity
     **/
    public void insertHomework(Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        contentValues.put(HOMEWORKS_COLOR, homework.getColor());
        db.insert(HOMEWORKS,null, contentValues);
        db.close();
    }

    public void updateHomework(Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        contentValues.put(HOMEWORKS_COLOR, homework.getColor());
        db.update(HOMEWORKS, contentValues, HOMEWORKS_ID + " = " + homework.getId(), null);
        db.close();
    }

    public void deleteHomeworkById(Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HOMEWORKS,HOMEWORKS_ID + " = ? ", new String[]{String.valueOf(homework.getId())});
        db.close();
    }


    public ArrayList<Homework> getHomework() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Homework> homelist = new ArrayList<>();
        Homework homework;
        Cursor cursor = db.rawQuery("SELECT * FROM "+ HOMEWORKS + " ORDER BY datetime(" + HOMEWORKS_DATE + ") ASC",null);
        while (cursor.moveToNext()){
            homework = new Homework();
            homework.setId(cursor.getInt(cursor.getColumnIndex(HOMEWORKS_ID)));
            homework.setSubject(cursor.getString(cursor.getColumnIndex(HOMEWORKS_SUBJECT)));
            homework.setDescription(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DESCRIPTION)));
            homework.setDate(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DATE)));
            homework.setColor(cursor.getInt(cursor.getColumnIndex(HOMEWORKS_COLOR)));
            homelist.add(homework);
        }
        cursor.close();
        db.close();
        return  homelist;
    }

    /**
     * Methods for Notes activity
     **/
    public void insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        contentValues.put(NOTES_COLOR, note.getColor());
        db.insert(NOTES, null, contentValues);
        db.close();
    }

    public void updateNote(Note note)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        contentValues.put(NOTES_COLOR, note.getColor());
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
            note.setColor(cursor.getInt(cursor.getColumnIndex(NOTES_COLOR)));
            notelist.add(note);
        }
        cursor.close();
        db.close();
        return notelist;
    }

    /**
     * Methods for Teachers activity
     **/
    public void insertTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEACHERS_NAME, teacher.getName());
        contentValues.put(TEACHERS_POST, teacher.getPost());
        contentValues.put(TEACHERS_PHONE_NUMBER, teacher.getPhonenumber());
        contentValues.put(TEACHERS_EMAIL, teacher.getEmail());
        contentValues.put(TEACHERS_COLOR, teacher.getColor());
        db.insert(TEACHERS, null, contentValues);
        db.close();
    }

    public void updateTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEACHERS_NAME, teacher.getName());
        contentValues.put(TEACHERS_POST, teacher.getPost());
        contentValues.put(TEACHERS_PHONE_NUMBER, teacher.getPhonenumber());
        contentValues.put(TEACHERS_EMAIL, teacher.getEmail());
        contentValues.put(TEACHERS_COLOR, teacher.getColor());
        db.update(TEACHERS, contentValues, TEACHERS_ID + " = " + teacher.getId(), null);
        db.close();
    }

    public void deleteTeacherById(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEACHERS, TEACHERS_ID + " =? ", new String[] {String.valueOf(teacher.getId())});
        db.close();
    }

    public ArrayList<Teacher> getTeacher() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Teacher> teacherlist = new ArrayList<>();
        Teacher teacher;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEACHERS, null);
        while (cursor.moveToNext()) {
            teacher = new Teacher();
            teacher.setId(cursor.getInt(cursor.getColumnIndex(TEACHERS_ID)));
            teacher.setName(cursor.getString(cursor.getColumnIndex(TEACHERS_NAME)));
            teacher.setPost(cursor.getString(cursor.getColumnIndex(TEACHERS_POST)));
            teacher.setPhonenumber(cursor.getString(cursor.getColumnIndex(TEACHERS_PHONE_NUMBER)));
            teacher.setEmail(cursor.getString(cursor.getColumnIndex(TEACHERS_EMAIL)));
            teacher.setColor(cursor.getInt(cursor.getColumnIndex(TEACHERS_COLOR)));
            teacherlist.add(teacher);
        }
        cursor.close();
        db.close();
        return teacherlist;
    }

    /**
     * Methods for Exams activity
     **/
    public void insertExam(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAMS_SUBJECT, exam.getSubject());
        contentValues.put(EXAMS_TEACHER, exam.getTeacher());
        contentValues.put(EXAMS_ROOM, exam.getRoom());
        contentValues.put(EXAMS_DATE, exam.getDate());
        contentValues.put(EXAMS_TIME, exam.getTime());
        contentValues.put(EXAMS_COLOR, exam.getColor());
        db.insert(EXAMS, null, contentValues);
        db.close();
    }

    public void updateExam(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAMS_SUBJECT, exam.getSubject());
        contentValues.put(EXAMS_TEACHER, exam.getTeacher());
        contentValues.put(EXAMS_ROOM, exam.getRoom());
        contentValues.put(EXAMS_DATE, exam.getDate());
        contentValues.put(EXAMS_TIME, exam.getTime());
        contentValues.put(EXAMS_COLOR, exam.getColor());
        db.update(EXAMS, contentValues, EXAMS_ID + " = " + exam.getId(), null);
        db.close();
    }

    public void deleteExamById(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXAMS, EXAMS_ID + " =? ", new String[] {String.valueOf(exam.getId())});
        db.close();
    }

    public ArrayList<Exam> getExam() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Exam> examslist = new ArrayList<>();
        Exam exam;
        Cursor cursor = db.rawQuery("SELECT * FROM " + EXAMS, null);
        while (cursor.moveToNext()) {
            exam = new Exam();
            exam.setId(cursor.getInt(cursor.getColumnIndex(EXAMS_ID)));
            exam.setSubject(cursor.getString(cursor.getColumnIndex(EXAMS_SUBJECT)));
            exam.setTeacher(cursor.getString(cursor.getColumnIndex(EXAMS_TEACHER)));
            exam.setRoom(cursor.getString(cursor.getColumnIndex(EXAMS_ROOM)));
            exam.setDate(cursor.getString(cursor.getColumnIndex(EXAMS_DATE)));
            exam.setTime(cursor.getString(cursor.getColumnIndex(EXAMS_TIME)));
            exam.setColor(cursor.getInt(cursor.getColumnIndex(EXAMS_COLOR)));
            examslist.add(exam);
        }
        cursor.close();
        db.close();
        return examslist;
    }
}
