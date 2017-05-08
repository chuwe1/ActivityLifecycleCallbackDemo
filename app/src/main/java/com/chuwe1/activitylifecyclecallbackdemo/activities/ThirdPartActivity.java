package com.chuwe1.activitylifecyclecallbackdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.chuwe1.activitylifecyclecallbackdemo.R;

/**
 * This class doesn't extend BaseActivity
 */
public class ThirdPartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //There isn't Log.e(getClass().getSimpleName(), "onCreate" + " --- called by self");

        setContentView(R.layout.activity_third_party);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //There isn't Log.e(getClass().getSimpleName(), "onDestroy" + " --- called by self");
    }
}
