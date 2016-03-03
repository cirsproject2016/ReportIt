package com.cirs.reportit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private ReportItApplication mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAppContext = (ReportItApplication) getApplicationContext();
        pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CIRSUser cirsUser = mAppContext.getCirsUser();
                if (pref.getBoolean(Constants.SPUD_IS_SIGNED_IN, false)) {
                    if (pref.getBoolean(Constants.SPUD_IS_PROFILE_CREATED, false)) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, CreateProfileActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, Constants.SPLASH_SCREEN_TIMEOUT);
    }

}
