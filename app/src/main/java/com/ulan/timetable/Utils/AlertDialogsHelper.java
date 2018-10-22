package com.ulan.timetable.Utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ulan.timetable.Adapters.WeekListAdapter;
import com.ulan.timetable.Model.Week;
import com.ulan.timetable.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulan on 22.10.2018.
 */
public class AlertDialogsHelper {

    public static void getEditDialog(final Context context, final View alertLayout, final WeekListAdapter adapter, int listposition) {
        final HashMap<String, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subject_dialog);
        editTextHashs.put("Subject", subject);
        final EditText teacher = alertLayout.findViewById(R.id.teacher_dialog);
        editTextHashs.put("Teacher", teacher);
        final EditText room = alertLayout.findViewById(R.id.room_dialog);
        editTextHashs.put("Room", room);
        final TextView from_time = alertLayout.findViewById(R.id.from_time);
        final TextView to_time = alertLayout.findViewById(R.id.to_time);
        final Week week = adapter.getWeeklist().get(listposition);

        subject.setText(adapter.getItem(listposition).getSubject());
        teacher.setText(adapter.getItem(listposition).getTeacher());
        room.setText(adapter.getItem(listposition).getRoom());
        from_time.setText(adapter.getItem(listposition).getFromTime());
        to_time.setText(adapter.getItem(listposition).getToTime());

        from_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                from_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                                week.setFromTime(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        to_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                to_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                                week.setToTime(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Edit subject");
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(subject.getText()) || TextUtils.isEmpty(teacher.getText()) || TextUtils.isEmpty(room.getText())) {
                    for (Map.Entry<String, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(entry.getKey() + " field can not be empty!");
                            entry.getValue().requestFocus();
                        }
                    }
                } else if(!from_time.getText().toString().matches(".*\\d+.*") || !to_time.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, "Time field can not be empty!", Snackbar.LENGTH_LONG).show();
                } else {
                    DbHelper db = new DbHelper(context);
                    week.setSubject(subject.getText().toString());
                    week.setTeacher(teacher.getText().toString());
                    week.setRoom(room.getText().toString());
                    db.updateWeek(week);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }
}
