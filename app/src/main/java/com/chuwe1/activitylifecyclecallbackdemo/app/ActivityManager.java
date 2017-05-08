package com.chuwe1.activitylifecyclecallbackdemo.app;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.Stack;

public class ActivityManager {

    /* -------------------Single instance start--------------------------- */
    private static ActivityManager instance;

    private ActivityManager() {
        activityStack = new Stack<>();
    }

    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }
    /* -------------------Single instance end--------------------------- */


    /* --------------------------- Activity stack start--------------------------- */
    private Stack<Activity> activityStack;

    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        for (Activity activity : activityStack) {
            finishActivity(activity);
        }
    }

    public void exitApp() {
        finishAllActivity();
        System.exit(0);
    }
    /* --------------------------- Activity stack end----------------------------- */


    /* -------------------ActivityLifecycleCallback start--------------------------- */
    private Application.ActivityLifecycleCallbacks activityLifecycleCallback;

    public Application.ActivityLifecycleCallbacks getActivityLifecycleCallback() {
        if (activityLifecycleCallback == null) {
            activityLifecycleCallback = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    addActivity(activity);
                    Log.e(activity.getClass().getSimpleName(), "was added");

                    Log.e(activity.getClass().getSimpleName(), "onCreated --- called by application");
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    Log.e(activity.getClass().getSimpleName(), "onStarted --- called by application");
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    Log.e(activity.getClass().getSimpleName(), "onResumed --- called by application");
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    Log.e(activity.getClass().getSimpleName(), "onPaused --- called by application");
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    Log.e(activity.getClass().getSimpleName(), "onStopped --- called by application");
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    Log.e(activity.getClass().getSimpleName(), "onSavedInstanceState --- called by application");
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    finishActivity(activity);
                    Log.e(activity.getClass().getSimpleName(), "was removed");

                    Log.e(activity.getClass().getSimpleName(), "onDestroyed --- called by application");
                }
            };
        }
        return activityLifecycleCallback;
    }
    /* -------------------ActivityLifecycleCallback end----------------------------- */
}
