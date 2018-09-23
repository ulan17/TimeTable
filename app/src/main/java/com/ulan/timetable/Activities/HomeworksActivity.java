package com.ulan.timetable.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.ulan.timetable.Adapters.HomeworksListAdapter;
import com.ulan.timetable.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.Week;

import java.util.ArrayList;
import java.util.Calendar;
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
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_add_homework, null);
        final EditText subject = alertLayout.findViewById(R.id.subjecthomework);
        final EditText description = alertLayout.findViewById(R.id.descriptionhomework);
        final TextView date = alertLayout.findViewById(R.id.datehomework);
        final Homework homework = new Homework();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeworksActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format("%02d:%02d:%02d", dayOfMonth, month+1, year));
                        homework.setDate(String.format("%02d:%02d:%02d", dayOfMonth, month+1, year));
                    }
                }, mYear, mMonth, mdayofMonth);
                datePickerDialog.setTitle("Select date");
                datePickerDialog.show();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add homework");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(subject.getText().toString().equals("") || !date.getText().toString().matches(".*\\d+.*") || description.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();

                } else {
                    DbHelper dbHelper = new DbHelper(HomeworksActivity.this);
                    homework.setSubject(subject.getText().toString());
                    homework.setDescription(description.getText().toString());
                    dbHelper.insertHomework(homework);
                    adapter.clear();
                    adapter.addAll(db.getHomework());
                    adapter.notifyDataSetChanged();
                }
                subject.setText("");
                description.setText("");
                date.setText(R.string.select_date);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }
}
