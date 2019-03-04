package com.ulan.timetable.model;

/**
 * Created by Ulan on 07.12.2018.
 */
public class Exam {
    private String subject, teacher, time, date, room;
    private int id, color;

    public Exam() {}

    public Exam(String subject, String teacher, String time, String date, String room, int color) {
        this.subject = subject;
        this.teacher = teacher;
        this.time = time;
        this.date = date;
        this.room = room;
        this.color = color;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
