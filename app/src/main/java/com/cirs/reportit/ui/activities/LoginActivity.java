package com.cirs.reportit.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cirs.reportit.utils.Constants;
import com.example.kshitij.reportit.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;

    private EditText edtPassword;

    private Button btnLogin;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private Context context = this;

    private String username;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                if (username.equals("user1") && password.equals("1234")) {
                    saveToSharedPref();
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, CreateProfileActivity.class));
                    finish();
                } else {
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(context, "Invalid username and password!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeViews() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void saveToSharedPref() {
        editor = pref.edit();
        editor.putString(Constants.SPUD_USERNAME, username);
        editor.putBoolean(Constants.SPUD_IS_SIGNED_IN, true);
        editor.commit();
    }
}