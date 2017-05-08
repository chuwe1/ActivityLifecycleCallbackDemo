package com.chuwe1.activitylifecyclecallbackdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chuwe1.activitylifecyclecallbackdemo.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToA(View view) {
        startActivity(new Intent(this, A.class));
    }

    public void goToB(View view) {
        startActivity(new Intent(this, B.class));
    }

    public void goToThirdParty(View view) {
        startActivity(new Intent(this, ThirdPartActivity.class));
    }
}
