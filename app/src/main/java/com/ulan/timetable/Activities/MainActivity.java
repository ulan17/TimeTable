package com.ulan.timetable.Activities;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ulan.timetable.Adapters.FragmentsTabAdapter;
import com.ulan.timetable.Fragments.FridayFragment;
import com.ulan.timetable.Fragments.MondayFragment;
import com.ulan.timetable.Fragments.SaturdayFragment;
import com.ulan.timetable.Fragments.SundayFragment;
import com.ulan.timetable.Fragments.ThursdayFragment;
import com.ulan.timetable.Fragments.TuesdayFragment;
import com.ulan.timetable.Fragments.WednesdayFragment;
import com.ulan.timetable.R;
import com.ulan.timetable.Utils.DbHelper;
import com.ulan.timetable.Week;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentsTabAdapter adapter;
    public ViewPager getViewPager() {
        return viewPager;
    }
    private ViewPager viewPager;
    private boolean switchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new FragmentsTabAdapter(getSupportFragmentManager());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initTabFragment();
        initCustomDialog();
        initSwitch(navigationView);

        if(switchState) changeTabFragments(true);

    }

    public void initTabFragment() {
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        adapter.addFragment(new MondayFragment(), "Monday");
        adapter.addFragment(new TuesdayFragment(), "Tuesday");
        adapter.addFragment(new WednesdayFragment(), "Wednesday");
        adapter.addFragment(new ThursdayFragment(), "Thursday");
        adapter.addFragment(new FridayFragment(), "Friday");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(day == 1 ? 6 : day-2, true);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_add_subject, null);
        final EditText subject = alertLayout.findViewById(R.id.subject_dialog);
        final EditText teacher = alertLayout.findViewById(R.id.teacher_dialog);
        final EditText room = alertLayout.findViewById(R.id.room_dialog);
        final TextView from_time = alertLayout.findViewById(R.id.from_time);
        final TextView to_time = alertLayout.findViewById(R.id.to_time);
        final Week week = new Week();

        from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                from_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                                week.setFromTime(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show(); }});

        to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                to_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                                week.setToTime(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add subject");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(subject.getText().toString().equals("") || !from_time.getText().toString().matches(".*\\d+.*") || !to_time.getText().toString().matches(".*\\d+.*") || room.getText().toString().equals("") || teacher.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();

                }
                else {
                    DbHelper dbHelper = new DbHelper(MainActivity.this);
                    week.setSubject(subject.getText().toString());
                    week.setFragment(adapter.getItem(viewPager.getCurrentItem()).toString());
                    week.setTeacher(teacher.getText().toString());
                    week.setRoom(room.getText().toString());
                    dbHelper.insertWeek(week);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
                subject.setText("");
                teacher.setText("");
                room.setText("");
                from_time.setText(R.string.select_time);
                to_time.setText(R.string.select_time);
            }
        });
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public void initSwitch(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.weeksettings);
        final View actionView = menuItem.getActionView();
        SwitchCompat switcher = (SwitchCompat) actionView.findViewById(R.id.drawer_switch);
        SharedPreferences sharedPref = getSharedPreferences("com.ulan.timetable", 0);
        switchState = sharedPref.getBoolean("isChecked", false);
        switcher.setChecked(switchState);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeTabFragments(isChecked);
                SharedPreferences sharedPref = getSharedPreferences("com.ulan.timetable", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isChecked", isChecked);
                editor.apply();
            }
        });
    }

    public void changeTabFragments(boolean isChecked) {
        if(isChecked) {
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            adapter.addFragment(new SaturdayFragment(), "Saturday");
            adapter.addFragment(new SundayFragment(), "Sunday");
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(day == 1 ? 6 : day-2, true);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            if(adapter.getFragmentList().size() > 5) {
                adapter.removeFragment(new SaturdayFragment(), 5);
                adapter.removeFragment(new SundayFragment(), 5);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "We're working on it. :)", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homework:
                Intent homework = new Intent(MainActivity.this, HomeworksActivity.class);
                startActivity(homework);
                return true;
            case R.id.notes:
                Intent note = new Intent(MainActivity.this, NotesActivity.class);
                startActivity(note);
                return true;
            case R.id.weeksettings:
                return true;
            default:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
    }
}
