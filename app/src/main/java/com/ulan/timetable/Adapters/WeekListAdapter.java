package com.ulan.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ulan.timetable.R;
import com.ulan.timetable.Week;

import java.util.ArrayList;


/**
 * Created by Ulan on 08.09.2018.
 */
public class WeekListAdapter extends ArrayAdapter<Week> {

    private static final String TAG = "WeekListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView subject;
        TextView time;
        TextView room;
    }

    public WeekListAdapter(Context context, int resource, ArrayList<Week> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String subject = getItem(position).getSubject();
        int time = getItem(position).getTime();
        String room = getItem(position).getRoom();

        Week week = new Week(subject, room);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.subject = (TextView) convertView.findViewById(R.id.subject);
           // holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.room = (TextView) convertView.findViewById(R.id.room);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        holder.subject.setText(week.getSubject());
        holder.room.setText(week.getRoom());

        return convertView;
    }
}
