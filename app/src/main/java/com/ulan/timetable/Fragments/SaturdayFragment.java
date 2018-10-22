package com.ulan.timetable.Fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ulan.timetable.Adapters.WeekListAdapter;
import com.ulan.timetable.Utils.AlertDialogsHelper;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.R;
import com.ulan.timetable.Model.Week;

import java.util.ArrayList;

public class SaturdayFragment extends Fragment {
    private DbHelper db;
    private ListView listView;
    private WeekListAdapter adapter;
    private int listposition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saturday, container, false);
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.saturdaylist);
        adapter = new WeekListAdapter(getActivity(), R.layout.listview_week_adapter, db.getWeek("Saturday"));
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
                            int key = checkedItems.keyAt(i);
                            if (checkedItems.get(key)) {
                                db.deleteWeekById(adapter.getItem(key));
                                removelist.add(adapter.getWeeklist().get(key));
                            }
                        }
                        adapter.getWeeklist().removeAll(removelist);
                        db.updateWeek(adapter.getWeek());
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;

                    case R.id.action_edit:
                        if(listView.getCheckedItemCount() == 1) {
                            final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
                            AlertDialogsHelper.getEditDialog(getContext(), alertLayout, adapter, listposition);
                        } else {
                            Snackbar.make(getView(), "Please, select one item.", Snackbar.LENGTH_LONG).show();
                        }
                        mode.finish();
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
