package com.ulan.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ulan.timetable.Model.Homework;
import com.ulan.timetable.R;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Ulan on 21.09.2018.
 */
public class HomeworksAdapter extends ArrayAdapter<Homework> {

    private Context mContext;
    private int mResource;
    private ArrayList<Homework> homeworklist;
    private Homework homework;

    private static class ViewHolder {
        TextView subject;
        TextView description;
        TextView date;
    }

    public HomeworksAdapter(Context context, int resource, ArrayList<Homework> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        homeworklist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String subject = Objects.requireNonNull(getItem(position)).getSubject();
        String description = Objects.requireNonNull(getItem(position)).getDescription();
        String date = Objects.requireNonNull(getItem(position)).getDate();

        homework = new Homework(subject, description, date);
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.subject = convertView.findViewById(R.id.subjecthomework);
            holder.description = convertView.findViewById(R.id.descriptionhomework);
            holder.date = convertView.findViewById(R.id.datehomework);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.subject.setText(homework.getSubject());
        holder.description.setText(homework.getDescription());
        holder.date.setText(homework.getDate());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<Homework> getHomeworkList() {
        return homeworklist;
    }

    public Homework getHomework() {
        return homework;
    }
}

