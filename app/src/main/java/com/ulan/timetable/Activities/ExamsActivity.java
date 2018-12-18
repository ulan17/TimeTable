package com.ulan.timetable.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ulan.timetable.Adapters.ExamsAdapter;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.AlertDialogsHelper;
import com.ulan.timetable.Utils.DbHelper;

public class ExamsActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listView;
    private ExamsAdapter adapter;
    private DbHelper db;
    private int listposition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
        initAll();
    }

    private void initAll() {
        setupAdapter();
//        setupListViewMultiSelect();
        setupCustomDialog();
    }

    private void setupAdapter() {
        db = new DbHelper(context);
        listView = findViewById(R.id.examslist);
        adapter = new ExamsAdapter(ExamsActivity.this, listView, R.layout.listview_exams_adapter, db.getExam());
        listView.setAdapter(adapter);
    }

    private void setupCustomDialog() {
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_exam, null);
        AlertDialogsHelper.getAddExamDialog(ExamsActivity.this, alertLayout, adapter);
    }
}
