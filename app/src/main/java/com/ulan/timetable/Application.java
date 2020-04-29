package com.ulan.timetable;


import android.os.Build;

import com.ulan.timetable.utils.ShortcutUtils;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Cyanea.init(this, getResources());
        if (Build.VERSION.SDK_INT >= 25) {
            ShortcutUtils.Companion.createShortcuts(getBaseContext());
        }
    }
}
