package com.ulan.timetable.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.WeekListAdapter;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.R;
import com.ulan.timetable.Week;

import java.util.ArrayList;

public class ThursdayFragment extends Fragment {
    private DbHelper db;
    private ListView listView;
    private WeekListAdapter adapter;
    private int listposition = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thursday, container, false);
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.thursdaylist);

        adapter = new WeekListAdapter(getActivity(), R.layout.adapter_listview_layout, db.getData("Thursday"));
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
                                db.deleteDataById(adapter.getItem(i).getId());
                                removelist.add(adapter.getWeeklist().get(i));
                            }
                        }
                        adapter.getWeeklist().removeAll(removelist);
                        db.updateData(adapter.getWeek());
                        checkedItems.clear();
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;

                    case R.id.action_edit:
                        if(listView.getCheckedItemCount() == 1) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Edit subject");
                            Context context = getContext();
                            final LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            final EditText subject = new EditText(getActivity());
                            layout.addView(subject);
                            final EditText teacher = new EditText(getActivity());
                            layout.addView(teacher);
                            final EditText room = new EditText(getActivity());
                            layout.addView(room);
                            final EditText time = new EditText(getActivity());
                            layout.addView(time);
                            builder.setView(layout);

                            subject.setText(adapter.getItem(listposition).getSubject());
                            teacher.setText(adapter.getItem(listposition).getTeacher());
                            room.setText(adapter.getItem(listposition).getRoom());
                            time.setText(adapter.getItem(listposition).getTime());

                            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (subject.getText().toString().equals("") || time.getText().toString().equals("") || room.getText().toString().equals("")) //name is the name of the Edittext in code
                                    {
                                        Toast.makeText(getContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Week week = adapter.getWeeklist().get(listposition);
                                        week.setSubject(subject.getText().toString());
                                        week.setTeacher(teacher.getText().toString());
                                        week.setRoom(room.getText().toString());
                                        week.setTime(time.getText().toString());
                                        db.updateData(week);
                                        adapter.notifyDataSetChanged();
                                        mode.finish();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            final AlertDialog ad = builder.create();
                            ad.show();
                        } else {
                            Toast.makeText(getActivity(), "Please, select one item", Toast.LENGTH_LONG).show();
                            mode.finish();
                        }
                        return true;
                }
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // not even need to inflate anything in order to ActionMode to appear
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
