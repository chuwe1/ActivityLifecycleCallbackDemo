package com.chuwe1.activitylifecyclecallbackdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(getClass().getSimpleName(), "onCreate --- called by self");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(getClass().getSimpleName(), "onStart --- called by self");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(getClass().getSimpleName(), "onResume --- called by self");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e(getClass().getSimpleName(), "onPause --- called by self");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e(getClass().getSimpleName(), "onStop --- called by self");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.e(getClass().getSimpleName(), "onSaveInstanceState --- called by self");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(getClass().getSimpleName(), "onDestroyed --- called by self");
    }
}
