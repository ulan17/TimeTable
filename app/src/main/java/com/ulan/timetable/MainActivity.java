package com.ulan.timetable;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ulan.timetable.Adapters.FragmentsTabAdapter;
import com.ulan.timetable.Fragments.FridayFragment;
import com.ulan.timetable.Fragments.MondayFragment;
import com.ulan.timetable.Fragments.SaturdayFragment;
import com.ulan.timetable.Fragments.SundayFragment;
import com.ulan.timetable.Fragments.ThursdayFragment;
import com.ulan.timetable.Fragments.TuesdayFragment;
import com.ulan.timetable.Fragments.WednesdayFragment;
import com.ulan.timetable.Utils.DbHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentsTabAdapter adapter;
    public ViewPager getViewPager() {
        return viewPager;
    }
    private ViewPager viewPager;
    private ListView listView;
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
        initDialog();
    }

    public void initTabFragment() {
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        adapter.addFragment(new MondayFragment(), "Monday");
        adapter.addFragment(new TuesdayFragment(), "Tuesday");
        adapter.addFragment(new WednesdayFragment(), "Wednesday");
        adapter.addFragment(new ThursdayFragment(), "Thursday");
        adapter.addFragment(new FridayFragment(), "Friday");
        adapter.addFragment(new SaturdayFragment(), "Saturday");
        adapter.addFragment(new SundayFragment(), "Sunday");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add subject");
        Context context = getApplicationContext();
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText subject = new EditText(this);
        subject.setHint("Subject");
        layout.addView(subject);
        final EditText room = new EditText(this);
        room.setHint("Room");
        layout.addView(room);
        final EditText time = new EditText(this);
        time.setHint("Time");
        layout.addView(time);
        builder.setView(layout);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(subject.getText().toString().equals("") || time.getText().toString().equals("") || room.getText().toString().equals("")) //name is the name of the Edittext in code
                {
                    Toast.makeText(getBaseContext(), "Please, fill in all the fields", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    DbHelper dbHelper = new DbHelper(MainActivity.this);
                    Week week = new Week();
                    week.setSubject(subject.getText().toString());
                    week.setFragment(adapter.getItem(viewPager.getCurrentItem()).toString());
                    week.setRoom(room.getText().toString());
                    week.setTime(time.getText().toString());
                    dbHelper.insertWeekDetails(week);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog ad = builder.create();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.show();
            }
        });
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.week) {
            // Handle the camera action
            // I am still working on it
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
