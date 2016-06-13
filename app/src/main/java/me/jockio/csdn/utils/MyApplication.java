package me.jockio.csdn.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by jockio on 16/6/11.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
