package com.ulan.timetable;

/**
 * Created by Ulan on 07.09.2018.
 */
public class Week {

    private String subject, fragment, room, time;
    private int id;

    public Week() {}

    public Week(String subject, String room, String time) {
        this.subject = subject;
        this.room = room;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String toString() {
        return subject;
    }
}
