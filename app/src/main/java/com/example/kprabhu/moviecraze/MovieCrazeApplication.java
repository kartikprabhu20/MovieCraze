package com.example.kprabhu.moviecraze;

import android.app.Application;
import android.content.Context;

public class MovieCrazeApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
