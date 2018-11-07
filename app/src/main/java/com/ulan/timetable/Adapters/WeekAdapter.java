package com.ulan.timetable.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulan.timetable.R;
import com.ulan.timetable.Model.Week;
import com.ulan.timetable.Utils.AlertDialogsHelper;
import com.ulan.timetable.Utils.DbHelper;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by Ulan on 08.09.2018.
 */
public class WeekAdapter extends ArrayAdapter<Week> {

    private Activity mActivity;
    private int mResource;
    private ArrayList<Week> weeklist;
    private Week week;

    private static class ViewHolder {
        TextView subject;
        TextView teacher;
        TextView time;
        TextView room;
        ImageView popup;
    }

    public WeekAdapter(Activity activity, int resource, ArrayList<Week> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mResource = resource;
        weeklist = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        String subject = Objects.requireNonNull(getItem(position)).getSubject();
        String teacher = Objects.requireNonNull(getItem(position)).getTeacher();
        String time_from = Objects.requireNonNull(getItem(position)).getFromTime();
        String time_to = Objects.requireNonNull(getItem(position)).getToTime();
        String room = Objects.requireNonNull(getItem(position)).getRoom();

        week = new Week(subject, teacher, room, time_from, time_to);
        final ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.subject = convertView.findViewById(R.id.subject);
            holder.teacher = convertView.findViewById(R.id.teacher);
            holder.time = convertView.findViewById(R.id.time);
            holder.room = convertView.findViewById(R.id.room);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.subject.setText(week.getSubject());
        holder.teacher.setText(week.getTeacher());
        holder.room.setText(week.getRoom());
        holder.time.setText(week.getFromTime() + " - " + week.getToTime());
        holder.popup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mActivity, holder.popup);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_popup:
                                DbHelper db = new DbHelper(mActivity);
                                db.deleteWeekById(getItem(position));
                                db.updateWeek(getItem(position));
                                weeklist.remove(position);
                                notifyDataSetChanged();
                                return true;

                            case R.id.edit_popup:
                                final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
                                AlertDialogsHelper.getEditSubjectDialog(mActivity, alertLayout, weeklist, position);
                                return true;
                                default:
                                    return onMenuItemClick(item);
                        }
                    }
                });
                popup.show();
            }
        });

        // holder.mConstraintLayout.setBackground(mActivity.getDrawable(week.getColor()));
        return convertView;
    }

    public ArrayList<Week> getWeekList() {
        return weeklist;
    }

    public Week getWeek() {
        return week;
    }

}
