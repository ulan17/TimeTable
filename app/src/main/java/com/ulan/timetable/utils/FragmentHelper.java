package com.ulan.timetable.utils;

import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ulan.timetable.adapters.WeekAdapter;
import com.ulan.timetable.model.Week;
import com.ulan.timetable.R;

import java.util.ArrayList;

/**
 * Created by Ulan on 03.12.2018.
 */
public class FragmentHelper {

    public static AbsListView.MultiChoiceModeListener setupListViewMultiSelect(final Activity activity, final ListView listView, final WeekAdapter adapter, final DbHelper db) {
        return new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount  = listView.getCheckedItemCount();
                mode.setTitle(checkedCount  + " " + activity.getResources().getString(R.string.selected));
                if(checkedCount == 0) mode.finish();
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
                                removelist.add(adapter.getWeekList().get(key));
                            }
                        }
                        adapter.getWeekList().removeAll(removelist);
                        db.updateWeek(adapter.getWeek());
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
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
        };
    }
}
