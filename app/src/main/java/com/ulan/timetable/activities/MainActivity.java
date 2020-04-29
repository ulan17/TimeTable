package com.ulan.timetable.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pd.chocobar.ChocoBar;
import com.ulan.timetable.R;
import com.ulan.timetable.adapters.FragmentsTabAdapter;
import com.ulan.timetable.fragments.WeekdayFragment;
import com.ulan.timetable.utils.AlertDialogsHelper;
import com.ulan.timetable.utils.DbHelper;
import com.ulan.timetable.utils.NotificationUtil;
import com.ulan.timetable.utils.PreferenceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentsTabAdapter adapter;
    private ViewPager viewPager;
    private boolean switchSevenDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralThemeNoActionBar(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        NotificationUtil.sendNotificationCurrentLesson(this, false);
        PreferenceUtil.setDoNotDisturb(this, PreferenceUtil.doNotDisturbDontAskAgain(this));
        initAll();
    }

    private void initAll() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        headerview.findViewById(R.id.nav_header_main_settings).setOnClickListener((View v) -> startActivity(new Intent(this, SettingsActivity.class)));
        TextView title = headerview.findViewById(R.id.nav_header_main_title);
        title.setText(R.string.activity_title);

        TextView desc = headerview.findViewById(R.id.nav_header_main_desc);
        desc.setText(R.string.nav_drawer_description);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setupSevenDaysPref();
        setupFragments();
        setupCustomDialog();

        if (switchSevenDays) changeFragments(true);
    }

    private void setupFragments() {
        adapter = new FragmentsTabAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        WeekdayFragment mondayFragment = new WeekdayFragment(WeekdayFragment.KEY_MONDAY_FRAGMENT);
        WeekdayFragment tuesdayFragment = new WeekdayFragment(WeekdayFragment.KEY_TUESDAY_FRAGMENT);
        WeekdayFragment wednesdayFragment = new WeekdayFragment(WeekdayFragment.KEY_WEDNESDAY_FRAGMENT);
        WeekdayFragment thursdayFragment = new WeekdayFragment(WeekdayFragment.KEY_THURSDAY_FRAGMENT);
        WeekdayFragment fridayFragment = new WeekdayFragment(WeekdayFragment.KEY_FRIDAY_FRAGMENT);

        adapter.addFragment(mondayFragment, getResources().getString(R.string.monday));
        adapter.addFragment(tuesdayFragment, getResources().getString(R.string.tuesday));
        adapter.addFragment(wednesdayFragment, getResources().getString(R.string.wednesday));
        adapter.addFragment(thursdayFragment, getResources().getString(R.string.thursday));
        adapter.addFragment(fridayFragment, getResources().getString(R.string.friday));

        viewPager.setAdapter(adapter);

        int day = getFragmentChoosingDay();
        viewPager.setCurrentItem(day == 1 ? 6 : day - 2, true);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void changeFragments(boolean isChecked) {
        if (isChecked) {
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            int day = getFragmentChoosingDay();
            adapter.addFragment(new WeekdayFragment(WeekdayFragment.KEY_SATURDAY_FRAGMENT), getResources().getString(R.string.saturday));
            adapter.addFragment(new WeekdayFragment(WeekdayFragment.KEY_SUNDAY_FRAGMENT), getResources().getString(R.string.sunday));
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(day == 1 ? 6 : day - 2, true);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            if (adapter.getFragmentList().size() > 5) {
                adapter.removeFragment(new WeekdayFragment(WeekdayFragment.KEY_SATURDAY_FRAGMENT), 5);
                adapter.removeFragment(new WeekdayFragment(WeekdayFragment.KEY_SUNDAY_FRAGMENT), 5);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private int getFragmentChoosingDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //If its after 18 o'clock, show the next day
        if (hour >= 18) {
            day++;
        }
        if (day > 7) { //Calender.Saturday
            day = day - 7; //1 = Calendar.Sunday, 2 = Calendar.Monday etc.
        }
        //If Saturday/Sunday are hidden, switch to Monday
        if (!switchSevenDays && (day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
            day = Calendar.MONDAY;
        }
        return day;
    }

    private void setupCustomDialog() {
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
        AlertDialogsHelper.getAddSubjectDialog(MainActivity.this, alertLayout, adapter, viewPager);
    }

    private void setupSevenDaysPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switchSevenDays = sharedPref.getBoolean(SettingsActivity.KEY_SEVEN_DAYS_SETTING, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
        } else if (item.getItemId() == R.id.action_backup) {
            backup();
        } else if (item.getItemId() == R.id.action_restore) {
            restore();
        } else if (item.getItemId() == R.id.action_remove_all) {
            deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.exams) {
            Intent exams = new Intent(MainActivity.this, ExamsActivity.class);
            startActivity(exams);
        } else if (itemId == R.id.homework) {
            Intent homework = new Intent(MainActivity.this, HomeworksActivity.class);
            startActivity(homework);
        } else if (itemId == R.id.notes) {
            Intent note = new Intent(MainActivity.this, NotesActivity.class);
            startActivity(note);
        } else if (itemId == R.id.settings) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
        } else if (itemId == R.id.schoolwebsitemenu) {
            String schoolWebsite = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_SCHOOL_WEBSITE_SETTING, null);
            if (!TextUtils.isEmpty(schoolWebsite)) {
                openUrlInChromeCustomTab(schoolWebsite);
            } else {
                ChocoBar.builder().setActivity(this)
                        .setText(getString(R.string.school_website_snackbar))
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .red()
                        .show();
            }
        } else if (itemId == R.id.teachers) {
            Intent teacher = new Intent(MainActivity.this, TeachersActivity.class);
            startActivity(teacher);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static final String filename = "Timetable_Backup.xls";

    @SuppressWarnings("deprecation")
    public void backup() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermission(this::backup, SheriffPermission.STORAGE);
            return;
        }

        String path = Environment.getExternalStoragePublicDirectory(Build.VERSION.SDK_INT >= 19 ? Environment.DIRECTORY_DOCUMENTS : Environment.DIRECTORY_DOWNLOADS).toString();
//        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd");
//        Date myDate = new Date();
//        String filename = timeStampFormat.format(myDate);

        AppCompatActivity activity = this;

        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(this, DbHelper.DB_NAME, path);
        sqliteToExcel.exportAllTables(filename, new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                runOnUiThread(() -> ChocoBar.builder().setActivity(activity)
                        .setText(getString(R.string.backup_successful, Build.VERSION.SDK_INT >= 19 ? getString(R.string.Documents) : getString(R.string.Downloads)))
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .green()
                        .show());
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> ChocoBar.builder().setActivity(activity)
                        .setText(getString(R.string.backup_failed))
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .red()
                        .show());
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void restore() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermission(this::restore, SheriffPermission.STORAGE);
            return;
        }

        String path = Environment.getExternalStoragePublicDirectory(Build.VERSION.SDK_INT >= 19 ? Environment.DIRECTORY_DOCUMENTS : Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + filename;
        File file = new File(path);
        if (!file.exists()) {
            ChocoBar.builder().setActivity(this)
                    .setText(getString(R.string.no_backup_found_in_downloads, Build.VERSION.SDK_INT >= 19 ? getString(R.string.Documents) : getString(R.string.Downloads)))
                    .setDuration(ChocoBar.LENGTH_LONG)
                    .red()
                    .show();
            return;
        }

        AppCompatActivity activity = this;
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.deleteAll();

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DbHelper.DB_NAME, false);
        excelToSQLite.importFromFile(path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                runOnUiThread(() -> ChocoBar.builder().setActivity(activity)
                        .setText(getString(R.string.import_successful))
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .green()
                        .show());
                MainActivity.this.onStart();
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> ChocoBar.builder().setActivity(activity)
                        .setText(getString(R.string.import_failed))
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .red()
                        .show());
            }
        });
    }

    public void deleteAll() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.remove_all_subjects))
                .content(getString(R.string.remove_all_subjects_content))
                .positiveText(getString(R.string.yes))
                .onPositive((dialog, which) -> {
                    try {
                        DbHelper dbHelper = new DbHelper(this);
                        dbHelper.deleteAll();
                        ChocoBar.builder().setActivity(this)
                                .setText(getString(R.string.remove_all_successful))
                                .setDuration(ChocoBar.LENGTH_LONG)
                                .green()
                                .show();
                        MainActivity.this.onStart();
                    } catch (Exception e) {
                        ChocoBar.builder().setActivity(this)
                                .setText(getString(R.string.remove_all_failed))
                                .setDuration(ChocoBar.LENGTH_LONG)
                                .red()
                                .show();
                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .negativeText(getString(R.string.no))
                .onNeutral((dialog, which) -> {
                    backup();
                    dialog.dismiss();
                })
                .neutralText(R.string.menu_backup)
                .show();
    }

    private void openUrlInChromeCustomTab(String url) {
        Context context = this;
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                    .setShowTitle(true)
                    .build();

            // This is optional but recommended
            CustomTabsHelper.Companion.addKeepAliveExtra(context, customTabsIntent.intent);

            // This is where the magic happens...
            CustomTabsHelper.Companion.openCustomTab(context, customTabsIntent,
                    Uri.parse(url),
                    new WebViewFallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Permissions
    private Sheriff sheriffPermission;
    private static final int REQUEST_MULTIPLE_PERMISSION = 101;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sheriffPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void requestPermission(Runnable runAfter, SheriffPermission... permissions) {
        PermissionListener pl = new MyPermissionListener(runAfter);

        sheriffPermission = Sheriff.Builder()
                .with(this)
                .requestCode(REQUEST_MULTIPLE_PERMISSION)
                .setPermissionResultCallback(pl)
                .askFor(permissions)
                .rationalMessage(getString(R.string.sheriff_permission_rational))
                .build();

        sheriffPermission.requestPermissions();
    }

    private class MyPermissionListener implements PermissionListener {
        final Runnable runAfter;

        MyPermissionListener(Runnable r) {
            runAfter = r;
        }

        @Override
        public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {
            if (runAfter == null)
                return;
            try {
                runAfter.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {
            // setup the alert builder
            MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this);
            builder.title(getString(R.string.permission_required));
            builder.content(getString(R.string.permission_required_description));

            // add the buttons
            builder.onPositive((dialog, which) -> {
                openAppPermissionSettings();
                dialog.dismiss();
            });
            builder.positiveText(getString(R.string.permission_ok_button));

            builder.negativeText(getString(R.string.permission_cancel_button));
            builder.onNegative((dialog, which) -> dialog.dismiss());

            // create and show the alert dialog
            MaterialDialog dialog = builder.build();
            dialog.show();
        }
    }

    private void openAppPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}
