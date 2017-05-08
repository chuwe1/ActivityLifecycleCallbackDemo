package com.chuwe1.activitylifecyclecallbackdemo.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chuwe1.activitylifecyclecallbackdemo.R;

public class B extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_b);
    }

    public void goToA(View view) {
        startActivity(new Intent(this, A.class));
    }

    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}

