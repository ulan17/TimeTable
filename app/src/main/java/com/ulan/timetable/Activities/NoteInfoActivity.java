package com.ulan.timetable.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ulan.timetable.Note;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;

public class NoteInfoActivity extends AppCompatActivity {

    private DbHelper db;
    private Note note;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_info);
        db = new DbHelper(NoteInfoActivity.this);
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("key");
        text = findViewById(R.id.edittextNote);
        if(note.getText() != null) {
            text.setText(note.getText());
        }
    }

    @Override
    public void onBackPressed() {
        note.setText(text.getText().toString());
        db.updateNote(note);
        Toast.makeText(NoteInfoActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                note.setText(text.getText().toString());
                db.updateNote(note);
                Toast.makeText(NoteInfoActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
