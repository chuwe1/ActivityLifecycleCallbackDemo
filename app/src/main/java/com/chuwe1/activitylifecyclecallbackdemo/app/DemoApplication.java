package com.chuwe1.activitylifecyclecallbackdemo.app;

import android.app.Application;
import android.util.Log;

public class DemoApplication extends Application {

    private static final String TAG = "Application";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "--- onCreate");

        registerActivityLifecycleCallbacks(ActivityManager.getInstance().getActivityLifecycleCallback());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e(TAG, "--- onTerminate");
        ActivityManager.getInstance().exitApp();
        unregisterActivityLifecycleCallbacks(ActivityManager.getInstance().getActivityLifecycleCallback());

    }
}
