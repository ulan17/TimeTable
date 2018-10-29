package com.ulan.timetable.Activities;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ulan.timetable.Adapters.HomeworksListAdapter;
import com.ulan.timetable.Model.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.AlertDialogsHelper;
import com.ulan.timetable.Utils.DbHelper;

import java.util.ArrayList;


public class HomeworksActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listView;
    private HomeworksListAdapter adapter;
    private DbHelper db;
    private int listposition = 0;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeworks);
        coordinatorLayout = findViewById(R.id.coordinatorHomeworks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DbHelper(context);
        listView = findViewById(R.id.homeworklist);
        adapter = new HomeworksListAdapter(context, R.layout.listview_homeworks_adapter, db.getHomework());
        listView.setAdapter(adapter);
        initCustomDialog();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                listposition = position;
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + "  Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.toolbar_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ArrayList<Homework> removelist = new ArrayList<>();
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for (int i = 0; i < checkedItems.size(); i++) {
                            int key = checkedItems.keyAt(i);
                            if (checkedItems.get(key)) {
                                db.deleteHomeworkById(adapter.getItem(key));
                                removelist.add(adapter.getHomeworklist().get(key));
                            }
                        }
                          adapter.getHomeworklist().removeAll(removelist);
                          db.updateHomework(adapter.getHomework());
                          adapter.notifyDataSetChanged();
                          mode.finish();
                        return true;

                    case R.id.action_edit:
                        if (listView.getCheckedItemCount() == 1) {
                            final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_homework, null);
                            AlertDialogsHelper.getEditHomeworkDialog(HomeworksActivity.this, alertLayout, adapter, listposition);
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.select_snackbar, Snackbar.LENGTH_LONG).show();
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    public void initCustomDialog() {
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_homework, null);
        AlertDialogsHelper.getAddHomeworkDialog(HomeworksActivity.this, alertLayout, adapter);
    }
}
