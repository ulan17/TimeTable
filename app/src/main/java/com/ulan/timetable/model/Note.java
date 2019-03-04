package com.ulan.timetable.model;


import java.io.Serializable;

/**
 * Created by Ulan on 28.09.2018.
 */
public class Note implements Serializable {
    private String title, text = "";
    private int id, color;

    public Note() {}

    public Note(String title, String text, int color) {
        this.title = title;
        this.text = text;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
