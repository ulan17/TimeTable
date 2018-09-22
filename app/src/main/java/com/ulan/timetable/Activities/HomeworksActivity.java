package com.ulan.timetable.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.ulan.timetable.Adapters.HomeworksListAdapter;
import com.ulan.timetable.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

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
