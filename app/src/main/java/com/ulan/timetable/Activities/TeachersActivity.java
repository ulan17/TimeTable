package com.ulan.timetable.Activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ulan.timetable.Model.Teacher;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

import java.util.Arrays;

public class TeachersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        initDialog();
    }

    public void initDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add teacher");
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(this);
        layout.addView(name);
        final EditText post = new EditText(this);
        layout.addView(post);
        final EditText phone = new EditText(this);
        layout.addView(phone);
        final EditText email = new EditText(this);
        layout.addView(email);
        dialog.setView(layout);

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DbHelper dbHelper = new DbHelper(getApplicationContext());
                Teacher teacher = new Teacher();
                teacher.setName(name.getText().toString());
                teacher.setPost(post.getText().toString());
                teacher.setPhonenumber(phone.getText().toString());
                teacher.setEmail(email.getText().toString());

                dbHelper.insertTeacher(teacher);
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
