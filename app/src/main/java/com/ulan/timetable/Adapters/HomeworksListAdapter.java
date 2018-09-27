package com.ulan.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ulan.timetable.Homework;
import com.ulan.timetable.R;
import java.util.ArrayList;

/**
 * Created by Ulan on 21.09.2018.
 */
public class HomeworksListAdapter extends ArrayAdapter<Homework> {

    private static final String TAG = "Homeworkslistadapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Homework> homeworklist;
    private Homework homework;

    private static class ViewHolder {
        TextView subject;
        TextView description;
        TextView date;
    }

    public HomeworksListAdapter(Context context, int resource, ArrayList<Homework> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        homeworklist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String subject = getItem(position).getSubject();
        String description = getItem(position).getDescription();
        String date = getItem(position).getDate();

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

    public ArrayList<Homework> getHomeworklist() {
        return homeworklist;
    }

    public Homework getHomework() {
        return homework;
    }
}

