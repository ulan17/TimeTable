package com.ulan.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ulan.timetable.Model.Teacher;
import com.ulan.timetable.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Ulan on 08.10.2018.
 */
public class TeachersAdapter extends ArrayAdapter<Teacher> {

    private Context mContext;
    private int mResource;
    private ArrayList<Teacher> teacherlist;
    private Teacher teacher;

    private static class ViewHolder {
        TextView name;
        TextView post;
        TextView phonenumber;
        TextView email;
    }

    public TeachersAdapter(Context context, int resource, ArrayList<Teacher> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        teacherlist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        String post = Objects.requireNonNull(getItem(position)).getPost();
        String phonenumber = Objects.requireNonNull(getItem(position)).getPhonenumber();
        String email = Objects.requireNonNull(getItem(position)).getEmail();


        teacher = new Teacher(name, post, phonenumber, email);
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = convertView.findViewById(R.id.nameteacher);
            holder.post = convertView.findViewById(R.id.postteacher);
            holder.phonenumber = convertView.findViewById(R.id.numberteacher);
            holder.email = convertView.findViewById(R.id.emailteacher);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(teacher.getName());
        holder.post.setText(teacher.getPost());
        holder.phonenumber.setText(teacher.getPhonenumber());
        holder.email.setText(teacher.getEmail());
        return convertView;
    }

    public ArrayList<Teacher> getTeacherList() {
        return teacherlist;
    }

    public Teacher getTeacher() {
        return teacher;
    }

}