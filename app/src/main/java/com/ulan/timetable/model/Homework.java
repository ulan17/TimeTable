package com.ulan.timetable.model;

/**
 * Created by Ulan on 21.09.2018.
 */
public class Homework {
    private String subject, description, date;
    private int id, color;

    public Homework() {}

    public Homework(String subject, String description, String date, int color) {
        this.subject = subject;
        this.description = description;
        this.date = date;
        this.color = color;
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
