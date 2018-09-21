package com.ulan.timetable;

/**
 * Created by Ulan on 21.09.2018.
 */
public class Homework {
    String subject, description, date;

    public Homework() {}

    Homework(String subject, String description, String date) {
        this.subject = subject;
        this.description = description;
        this.date = date;
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
}
