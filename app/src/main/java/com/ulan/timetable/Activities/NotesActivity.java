package com.ulan.timetable.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.NotesListAdapter;
import com.ulan.timetable.Note;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

public class NotesActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listview;
    private DbHelper db;
    private NotesListAdapter adapter;
    private int listposition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DbHelper(context);
        adapter = new NotesListAdapter(NotesActivity.this, R.layout.notes_listview_adapter, db.getNote());
        listview = findViewById(R.id.notelist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, NoteInfoActivity.class);
                intent.putExtra("key", adapter.getNotelist().get(position));
                startActivity(intent);
            }
        });
        initCustomDialog();
    }

    private void initCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_add_note, null);
        final EditText title = alertLayout.findViewById(R.id.titlenote);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Add note");
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        final Note note = new Note();

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(title.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    DbHelper dbHelper = new DbHelper(context);
                    note.setTitle(title.getText().toString());
                    dbHelper.insertNote(note);
                    adapter.clear();
                    adapter.addAll(dbHelper.getNote());
                    adapter.notifyDataSetChanged();
                }
                title.setText("");
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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.addAll(db.getNote());
        adapter.notifyDataSetChanged();
    }
}
