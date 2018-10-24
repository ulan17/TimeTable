package com.ulan.timetable.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.HomeworksListAdapter;
import com.ulan.timetable.Model.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                            LayoutInflater inflater = getLayoutInflater();
                            final View alertLayout = inflater.inflate(R.layout.dialog_add_homework, null);
                            final HashMap<String, EditText> editTextHashs = new HashMap<>();
                            final EditText subject = alertLayout.findViewById(R.id.subjecthomework);
                            editTextHashs.put("Subject", subject);
                            final EditText description = alertLayout.findViewById(R.id.descriptionhomework);
                            editTextHashs.put("Description", description);
                            final TextView date = alertLayout.findViewById(R.id.datehomework);
                            final Homework homework = adapter.getHomeworklist().get(listposition);

                            subject.setText(adapter.getItem(listposition).getSubject());
                            description.setText(adapter.getItem(listposition).getDescription());
                            date.setText(adapter.getItem(listposition).getDate());

                            date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Calendar calendar = Calendar.getInstance();
                                    int mYear = calendar.get(Calendar.YEAR);
                                    int mMonth = calendar.get(Calendar.MONTH);
                                    int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            date.setText(String.format("%02d:%02d:%02d", dayOfMonth, month + 1, year));
                                            homework.setDate(String.format("%02d:%02d:%02d", dayOfMonth, month + 1, year));
                                        }
                                    }, mYear, mMonth, mdayofMonth);
                                    datePickerDialog.setTitle("Select date");
                                    datePickerDialog.show();
                                }
                            });

                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Edit homework");
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
                                    if(TextUtils.isEmpty(subject.getText()) || TextUtils.isEmpty(description.getText())) {
                                        for (Map.Entry<String, EditText> editText : editTextHashs.entrySet()) {
                                            if(TextUtils.isEmpty(editText.getValue().getText())) {
                                                editText.getValue().setError(editText.getKey() + " field can not be empty!");
                                                editText.getValue().requestFocus();
                                            }
                                        }
                                    } else if(!date.getText().toString().matches(".*\\d+.*")) {
                                        Snackbar.make(alertLayout, "Date field can not be empty!", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        DbHelper dbHelper = new DbHelper(context);
                                        homework.setSubject(subject.getText().toString());
                                        homework.setDescription(description.getText().toString());
                                        dbHelper.updateHomework(homework);
                                        adapter.notifyDataSetChanged();
                                        mode.finish();
                                        dialog.dismiss();
                                    }
                                }
                            });
                            return true;
                        } else {
                            Snackbar.make(coordinatorLayout, "Please, select one item.", Snackbar.LENGTH_LONG).show();
                            mode.finish();
                        }
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
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialog_add_homework, null);
        final HashMap<String, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subjecthomework);
        editTextHashs.put("Subject", subject);
        final EditText description = alertLayout.findViewById(R.id.descriptionhomework);
        editTextHashs.put("Description", description);
        final TextView date = alertLayout.findViewById(R.id.datehomework);
        final Homework homework = new Homework();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Add homework");
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(subject.getText()) || TextUtils.isEmpty(description.getText())) {
                    for (Map.Entry<String, EditText> editText : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(editText.getValue().getText())) {
                            editText.getValue().setError(editText.getKey() + " field can not be empty!");
                            editText.getValue().requestFocus();
                        }
                    }
                } else if(!date.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, "Deadline field can not be empty!", Snackbar.LENGTH_LONG).show();
                } else {
                    DbHelper dbHelper = new DbHelper(context);
                    homework.setSubject(subject.getText().toString());
                    homework.setDescription(description.getText().toString());
                    dbHelper.insertHomework(homework);
                    adapter.clear();
                    adapter.addAll(db.getHomework());
                    adapter.notifyDataSetChanged();

                    subject.getText().clear();
                    description.getText().clear();
                    date.setText(R.string.select_date);
                    subject.requestFocus();
                    dialog.dismiss();
                }
            }
        });
    }
}
