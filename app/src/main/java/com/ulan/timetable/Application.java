package com.ulan.timetable;

import com.jaredrummler.cyanea.Cyanea;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Cyanea.init(this, getResources());
    }
}
