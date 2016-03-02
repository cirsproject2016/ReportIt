package com.cirs.reportit.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.VolleyRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;

    private EditText edtPassword;

    private Button btnLogin;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private Context mActivityContext = this;

    private String username;

    private String password;

    private Long adminId;

    private ReportItApplication mAppContext;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAppContext = (ReportItApplication) getApplicationContext();

        initializeViews();

        pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCredentials();
            }
        });
    }

    private void initializeViews() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setMessage("Verifying credentials");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void saveToSharedPref() {
        editor = pref.edit();
        editor.putLong(Constants.SPUD_ADMIN_ID, adminId);
        editor.putString(Constants.SPUD_USERNAME, username);
        editor.putBoolean(Constants.SPUD_IS_SIGNED_IN, true);
        editor.commit();
    }

    private void validateCredentials() {
        progressDialog.show();
        username = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        UserCred obj = new UserCred();
        obj.userName = username;
        obj.password = password;
        new VolleyRequest<CIRSUser>(mActivityContext).makeGsonRequest(
                Request.Method.POST,
                Constants.BASE_URI + "/user",
                obj,
                new Response.Listener<CIRSUser>() {
                    @Override
                    public void onResponse(CIRSUser response) {
                        adminId = response.getAdmin().getId();
                        mAppContext.setCirsUser(response);
                        saveToSharedPref();
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        if (!pref.getBoolean(Constants.SPUD_IS_PROFILE_CREATED, false)) {
                            startActivity(new Intent(LoginActivity.this, CreateProfileActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                        progressDialog.dismiss();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        edtUsername.setText("");
                        edtPassword.setText("");
                        System.out.println(error.toString());
                        Toast.makeText(mActivityContext, "Invalid userName and password!", Toast.LENGTH_LONG).show();
                    }
                },
                CIRSUser.class);
    }

    private class UserCred {
        String userName;
        String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {

            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}