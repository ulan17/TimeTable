package com.ulan.timetable.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ulan.timetable.Homework;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

public class HomeworksActivity extends AppCompatActivity {
    private Context context = this;
    private Homework homework;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeworks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        DbHelper db = new DbHelper(getApplicationContext());
                        homework = new Homework();
                        homework.setSubject(subject.getText().toString());
                        homework.setDescription(description.getText().toString());
                        homework.setDate(date.getText().toString());
                        db.insertHomework(homework);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        DbHelper db = new DbHelper(getApplicationContext());
        TextView textView = findViewById(R.id.homeworks);
        textView.setText(db.getHomework().get(0).getSubject() + " " + db.getHomework().get(0).getDescription() + " " + db.getHomework().get(0).getDate());
    }
}
