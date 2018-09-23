package com.ulan.timetable.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.ulan.timetable.Adapters.HomeworksListAdapter;
import com.ulan.timetable.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.Week;

import java.util.ArrayList;
import java.util.Objects;

public class HomeworksActivity extends AppCompatActivity {
    private Context context = this;
    private ListView listView;
    private HomeworksListAdapter adapter;
    private DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeworks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DbHelper(HomeworksActivity.this);
        listView = findViewById(R.id.homeworklist);
        adapter = new HomeworksListAdapter(HomeworksActivity.this, R.layout.homework_listview_adapter, db.getHomework());
        listView.setAdapter(adapter);
        initCustomDialog();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount  = listView.getCheckedItemCount();
                mode.setTitle(checkedCount  + "  Selected");
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
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ArrayList<Homework> removelist = new ArrayList<>();
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for (int i = 0; i < checkedItems.size(); i++) {
                            if (checkedItems.valueAt(i)) {
                                db.deleteHomeworkById(Objects.requireNonNull(adapter.getItem(i)).getId());
                                removelist.add(adapter.getHomeworklist().get(i));
                            }
                        }
                          adapter.getHomeworklist().removeAll(removelist);
                          db.updateHomework(adapter.getHomework());
                          adapter.notifyDataSetChanged();
                          mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    public void initCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add homework");
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText subject = new EditText(context);
        layout.addView(subject);
        final EditText description = new EditText(context);
        layout.addView(description);
        final EditText date = new EditText(context);
        layout.addView(date);
        builder.setView(layout);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Homework homework = new Homework();
                DbHelper dbHelper = new DbHelper(HomeworksActivity.this);
                homework.setSubject(subject.getText().toString());
                homework.setDescription(description.getText().toString());
                homework.setDate(date.getText().toString());
                dbHelper.insertHomework(homework);
                adapter.clear();
                adapter.addAll(db.getHomework());
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }
}
