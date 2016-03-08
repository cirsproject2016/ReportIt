package com.cirs.reportit.ui.activities;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.adapters.UserComplaintsAdapter;
import com.cirs.reportit.utils.CircularNetworkImageView;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.ArrayList;

public class ViewUserActivity extends AppCompatActivity {

    private long userId;

    private Context mActivityContext = this;

    private TextView txtUserFullName, txtGender, txtDOB, txtEmail, txtPhone;

    private CIRSUser cirsUser;

    private ArrayList<Complaint> userComplaints;

    private CircularNetworkImageView imgProfile;

    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(ViewUserActivity.this, "Could not retrieve user details", Toast.LENGTH_LONG).show();
            finish();
        }
        initializeViews();
        new VolleyRequest<CIRSUser>(mActivityContext).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoGetUserById(userId),
                null,
                new Response.Listener<CIRSUser>() {
                    @Override
                    public void onResponse(CIRSUser response) {
                        cirsUser = response;
                        userComplaints = new ArrayList<Complaint>(response.getComplaints());
                        setFields();
                        setRecyclerViewHeight();
                        recyclerView.setAdapter(new UserComplaintsAdapter(userComplaints, mActivityContext));
                        getProfilePicFromServer();
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                        collapsingToolbarLayout.setTitle(cirsUser.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivityContext, "Could not retrieve user details", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                CIRSUser.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeViews() {
        txtUserFullName = (TextView) findViewById(R.id.txt_user_name);
        txtGender = (TextView) findViewById(R.id.txt_gender);
        txtDOB = (TextView) findViewById(R.id.txt_dob);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        imgProfile = (CircularNetworkImageView) findViewById(R.id.img_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivityContext);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setFields() {
        txtUserFullName.setText(cirsUser.toString());
        txtGender.setText(cirsUser.getGender());
        txtDOB.setText(cirsUser.getDob());
        txtEmail.setText(cirsUser.getEmail());
        txtPhone.setText(cirsUser.getPhone());
    }

    private void getProfilePicFromServer() {
        String URL = Generator.getURLtoGetUserImage(userId);
        ImageLoader imageLoader = VolleyImageRequest.getInstance(mActivityContext).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                imgProfile, R.drawable.ic_my_profile, android.R.drawable.ic_dialog_alert));
        imgProfile.setImageUrl(URL, imageLoader);
    }

    private void setRecyclerViewHeight() {
        final float scale = mActivityContext.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int) (userComplaints.size() * 30 * scale + 0.5f);
        recyclerView.setLayoutParams(params);
    }

}
