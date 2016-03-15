package com.cirs.reportit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.ErrorUtils;
import com.pixplicity.easyprefs.library.Prefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Show user if hes not connected
        ErrorUtils.showErrorIfNotConnected(this);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CIRSUser cirsUser = ReportItApplication.getCirsUser();
                if (Prefs.getBoolean(Constants.SPUD_IS_SIGNED_IN, false)) {
                    if (Prefs.getBoolean(Constants.SPUD_IS_PROFILE_CREATED, false)) {
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
