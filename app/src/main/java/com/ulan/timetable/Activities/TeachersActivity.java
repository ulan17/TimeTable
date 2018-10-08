package com.ulan.timetable.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.TeachersListAdapter;
import com.ulan.timetable.Model.Teacher;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;


public class TeachersActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listView;
    private DbHelper db;
    private TeachersListAdapter adapter;
    private int listposition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        db = new DbHelper(context);
        adapter = new TeachersListAdapter(context, R.layout.listview_teachers_adapter, db.getTeacher());
        listView = findViewById(R.id.teacherlist);
        listView.setAdapter(adapter);

        initCustomDialog();
    }

    public void initCustomDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View alertLayout = layoutInflater.inflate(R.layout.dialog_add_teacher, null);
        final EditText name = alertLayout.findViewById(R.id.name_dialog);
        final EditText post = alertLayout.findViewById(R.id.post_dialog);
        final EditText phonenumber = alertLayout.findViewById(R.id.phonenumber_dialog);
        final EditText email = alertLayout.findViewById(R.id.email_dialog);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add teacher");
        dialog.setCancelable(false);
        dialog.setView(alertLayout);

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(name.getText().toString().equals("") || post.getText().toString().equals("") || phonenumber.getText().toString().equals("") || email.getText().toString().equals("")) {
                    Toast.makeText(getBaseContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();

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
                }
                name.setText("");
                post.setText("");
                phonenumber.setText("");
                email.setText("");
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = dialog.create();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });
    }
}
