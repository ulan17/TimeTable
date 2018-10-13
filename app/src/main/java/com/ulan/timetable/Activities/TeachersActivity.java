package com.ulan.timetable.Activities;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.TeachersListAdapter;
import com.ulan.timetable.Model.Teacher;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TeachersActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listView;
    private DbHelper db;
    private TeachersListAdapter adapter;
    private int listposition = 0;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        coordinatorLayout = findViewById(R.id.coordinatorTeachers);
        db = new DbHelper(context);
        adapter = new TeachersListAdapter(context, R.layout.listview_teachers_adapter, db.getTeacher());
        listView = findViewById(R.id.teacherlist);
        listView.setAdapter(adapter);

        initCustomDialog();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                listposition = position;
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
                        ArrayList<Teacher> removelist = new ArrayList<>();
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for(int i = 0; i < checkedItems.size(); i++) {
                            int key = checkedItems.keyAt(i);
                            if (checkedItems.get(key)) {
                                db.deleteTeacherById(adapter.getItem(key));
                                removelist.add(adapter.getTeacherlist().get(key));
                            }
                        }
                        adapter.getTeacherlist().removeAll(removelist);
                        db.updateTeacher(adapter.getTeacher());
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;
                    case R.id.action_edit:
                        if(listView.getCheckedItemCount() == 1) {
                            LayoutInflater layoutInflater = getLayoutInflater();
                            final View alertLayout = layoutInflater.inflate(R.layout.dialog_add_teacher, null);
                            final HashMap<String, EditText> editTextHashs = new HashMap<>();
                            final EditText name = alertLayout.findViewById(R.id.name_dialog);
                            editTextHashs.put("Name", name);
                            final EditText post = alertLayout.findViewById(R.id.post_dialog);
                            editTextHashs.put("Post", post);
                            final EditText phonenumber = alertLayout.findViewById(R.id.phonenumber_dialog);
                            editTextHashs.put("Phone", phonenumber);
                            final EditText email = alertLayout.findViewById(R.id.email_dialog);
                            editTextHashs.put("Email", email);
                            final Teacher teacher = adapter.getTeacherlist().get(listposition);

                            name.setText(teacher.getName());
                            post.setText(teacher.getPost());
                            phonenumber.setText(teacher.getPhonenumber());
                            email.setText(teacher.getEmail());

                            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Edit teacher");
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
                                    if(TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(post.getText()) || TextUtils.isEmpty(phonenumber.getText()) || TextUtils.isEmpty(email.getText())) {
                                        for (Map.Entry<String, EditText> entry : editTextHashs.entrySet()) {
                                            if(TextUtils.isEmpty(entry.getValue().getText())) {
                                                entry.getValue().setError(entry.getKey() + " field can not be empty!");
                                                entry.getValue().requestFocus();
                                            }
                                        }
                                    } else {
                                        DbHelper dbHelper = new DbHelper(context);
                                        teacher.setName(name.getText().toString());
                                        teacher.setPost(post.getText().toString());
                                        teacher.setPhonenumber(phonenumber.getText().toString());
                                        teacher.setEmail(email.getText().toString());
                                        dbHelper.updateTeacher(teacher);
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        mode.finish();
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
        LayoutInflater layoutInflater = getLayoutInflater();
        final View alertLayout = layoutInflater.inflate(R.layout.dialog_add_teacher, null);
        final HashMap<String, EditText> editTextHashs = new HashMap<>();
        final EditText name = alertLayout.findViewById(R.id.name_dialog);
        editTextHashs.put("Name", name);
        final EditText post = alertLayout.findViewById(R.id.post_dialog);
        editTextHashs.put("Post", post);
        final EditText phonenumber = alertLayout.findViewById(R.id.phonenumber_dialog);
        editTextHashs.put("Phone", phonenumber);
        final EditText email = alertLayout.findViewById(R.id.email_dialog);
        editTextHashs.put("Email", email);

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Add teacher");
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
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
                if(TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(post.getText()) || TextUtils.isEmpty(phonenumber.getText()) || TextUtils.isEmpty(email.getText())) {
                    for (Map.Entry<String, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(entry.getKey() + " field can not be empty!");
                            entry.getValue().requestFocus();
                        }
                    }
                } else {
                    DbHelper dbHelper = new DbHelper(getApplicationContext());
                    Teacher teacher = new Teacher();
                    teacher.setName(name.getText().toString());
                    teacher.setPost(post.getText().toString());
                    teacher.setPhonenumber(phonenumber.getText().toString());
                    teacher.setEmail(email.getText().toString());
                    dbHelper.insertTeacher(teacher);
                    adapter.clear();
                    adapter.addAll(dbHelper.getTeacher());
                    adapter.notifyDataSetChanged();

                    name.setText("");
                    post.setText("");
                    phonenumber.setText("");
                    email.setText("");

                    dialog.dismiss();
                }
            }
        });
    }
}
