package com.ulan.timetable.Fragments;

import android.os.Bundle;
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
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.R;
import com.ulan.timetable.Week;

import java.util.ArrayList;

public class WednesdayFragment extends Fragment {
    private DbHelper db;
    private ListView listView;
    private WeekListAdapter adapter;
    private int listposition = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wednesday, container, false);
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.wednesdaylist);

        adapter = new WeekListAdapter(getActivity(), R.layout.adapter_listview_layout, db.getData("Wednesday"));
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
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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
                        adapter.notifyDataSetChanged();
                        mode.finish();
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
