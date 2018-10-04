package com.ulan.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ulan.timetable.R;
import com.ulan.timetable.Model.Week;

import java.util.ArrayList;


/**
 * Created by Ulan on 08.09.2018.
 */
public class WeekListAdapter extends ArrayAdapter<Week> {

    private static final String TAG = "WeekListAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Week> weeklist;
    private Week week;

    private static class ViewHolder {
        TextView subject;
        TextView teacher;
        TextView time;
        TextView room;
    }

    public WeekListAdapter(Context context, int resource, ArrayList<Week> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        weeklist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String subject = getItem(position).getSubject();
        String teacher = getItem(position).getTeacher();
        String time_from = getItem(position).getFromTime();
        String time_to = getItem(position).getToTime();
        String room = getItem(position).getRoom();

        week = new Week(subject, teacher, room, time_from, time_to);
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.subject = convertView.findViewById(R.id.subject);
            holder.teacher = convertView.findViewById(R.id.teacher);
            holder.time = convertView.findViewById(R.id.time);
            holder.room = convertView.findViewById(R.id.room);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.subject.setText(week.getSubject());
        holder.teacher.setText(week.getTeacher());
        holder.room.setText(week.getRoom());
        holder.time.setText(week.getFromTime() + " - " + week.getToTime());
        return convertView;
    }

    public ArrayList<Week> getWeeklist() {
        return weeklist;
    }

    public Week getWeek() {
        return week;
    }

}
