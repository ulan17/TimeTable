package com.ulan.timetable.model;

/**
 * Created by Ulan on 07.10.2018.
 */
public class Teacher {
    private String name, post, phonenumber, email;
    private int id, color;

    public Teacher() {
    }
    public Teacher(String name, String post, String phonenumber, String email, int color) {
        this.name = name;
        this.post = post;
        this.phonenumber = phonenumber;
        this.email = email;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
