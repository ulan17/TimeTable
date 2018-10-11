package com.ulan.timetable.Fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ulan.timetable.Adapters.WeekListAdapter;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.R;
import com.ulan.timetable.Model.Week;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FridayFragment extends Fragment {
    private DbHelper db;
    private ListView listView;
    private WeekListAdapter adapter;
    private int listposition = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friday, container, false);
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.fridaylist);

        adapter = new WeekListAdapter(getActivity(), R.layout.listview_week_adapter, db.getWeek("Friday"));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                listposition = position;
                final int checkedCount  = listView.getCheckedItemCount();
                mode.setTitle(checkedCount  + "  Selected");
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ArrayList<Week> removelist = new ArrayList<>();
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for (int i = 0; i < checkedItems.size(); i++) {
                            if (checkedItems.valueAt(i)) {
                                db.deleteWeekById(adapter.getItem(i).getId());
                                removelist.add(adapter.getWeeklist().get(i));
                            }
                        }
                        adapter.getWeeklist().removeAll(removelist);
                        db.updateWeek(adapter.getWeek());
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;

                    case R.id.action_edit:
                        if(listView.getCheckedItemCount() == 1) {
                            LayoutInflater inflater = getLayoutInflater();
                            final View alertLayout = inflater.inflate(R.layout.dialog_add_subject, null);
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
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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

                            final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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
                                        week.setSubject(subject.getText().toString());
                                        week.setTeacher(teacher.getText().toString());
                                        week.setRoom(room.getText().toString());
                                        db.updateWeek(week);
                                        adapter.notifyDataSetChanged();
                                        mode.finish();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Snackbar.make(getView(), "Please, select one item.", Snackbar.LENGTH_LONG).show();
                            mode.finish();
                        }
                        return true;
                }
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.toolbar_action_mode, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });
        return view;
    }

}
