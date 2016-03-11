package com.cirs.reportit.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;
import com.cirs.reportit.utils.VolleyRequest;

public class ViewComplaintActivity extends AppCompatActivity {

    public static final String EXTRA_COMPLAINT_ID = "complaintId";

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private FloatingActionButton floatingActionButton;

    private boolean isBookmarked = false;

    private NetworkImageView imgComplaint;

    private AppBarLayout appBarLayout;

    private Toolbar toolbar;

    private Complaint complaint;

    private Context mActivityContext = this;

    private TextView txtStatus, txtCategory, txtComplainant, txtDescription, txtLocation, txtLandmark, txtPostedOn;

    private ProgressDialog progressDialog;

    private Button btnUpvote, btnComment;

    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint);

        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        new VolleyRequest<Complaint>(mActivityContext).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoGetComplaintById(getIntent().getLongExtra(EXTRA_COMPLAINT_ID, -1)),
                null,
                new Response.Listener<Complaint>() {
                    @Override
                    public void onResponse(Complaint response) {
                        complaint = response;
                        initializeViews();
                        setListeners();
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mActivityContext, "There was an error loading the requested complaint", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        if (error instanceof TimeoutError) {
                            Log.d("ViewComplaintActivity", "Timeouterror");
                            return;
                        }
                        finish();
                    }
                },
                Complaint.class);
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

    private boolean isBookmarked() {
        return isBookmarked;
    }

    private void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    private void rotate(View v, boolean clockwise) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        float angle;

        if (clockwise) angle = 144.0f;
        else angle = -144.0f;

        final RotateAnimation animRotate = new RotateAnimation(0.0f, angle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(500);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        v.startAnimation(animSet);
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_bookmark);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        imgComplaint = (NetworkImageView) findViewById(R.id.img_complaint);

        txtCategory = (TextView) findViewById(R.id.txt_category);
        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtComplainant = (TextView) findViewById(R.id.txt_complainant);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        txtLandmark = (TextView) findViewById(R.id.txt_landmark);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPostedOn = (TextView) findViewById(R.id.txt_posted_on);
        txtTitle = (TextView) findViewById(R.id.txt_title);

        String URL = Generator.getURLtoGetComplaintImage(complaint.getId());
        ImageLoader imageLoader = VolleyImageRequest.getInstance(mActivityContext).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                this.imgComplaint, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_dialog_alert));
        imgComplaint.setImageUrl(URL, imageLoader);

        setStatus(complaint.getStatus());
        txtCategory.setText(complaint.getCategory().toString());
        setTxtComplainant();
        txtDescription.setText(complaint.getDescription());
        txtLocation.setText(complaint.getLocation());
        txtLandmark.setText(complaint.getLandmark());
        txtPostedOn.setText(complaint.getTimestamp().toString());
        txtTitle.setText(complaint.getTitle());

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setTitle(complaint.getTitle());
//        Set height of AppBarLayout equal to width of screen for ImageView to be a square
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = (int) screenWidth;

        btnComment = (Button) findViewById(R.id.btn_comment);
        btnUpvote = (Button) findViewById(R.id.btn_upvote);
        btnComment.setText(complaint.getCommentsCount() + "");
        btnUpvote.setText(complaint.getUpvotes() + "");
    }

    private void setStatus(String status) {
        this.txtStatus.setText(status);
        int resId = android.R.color.white;
        switch (status) {
            case "PENDING":
                resId = android.R.color.holo_red_dark;
                break;
            case "INPROGRESS":
                this.txtStatus.setText("IN PROGRESS");
                resId = android.R.color.holo_orange_dark;
                break;
            case "COMPLETED":
                resId = android.R.color.holo_green_dark;
                break;
            case "REJECTED":
                resId = android.R.color.black;
                break;
            case "DUPLICATE":
                resId = android.R.color.holo_blue_dark;
                break;
        }
        this.txtStatus.setTextColor(getResources().getColor(resId));
    }

    private void setTxtComplainant() {
        if (complaint.getUser().getId() == ReportItApplication.getCirsUser().getId()) {
            txtComplainant.setText("You");
        } else {
            txtComplainant.setText(Html.fromHtml("<u>" + complaint.getUser().toString() + "</u>"));
            txtComplainant.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            txtComplainant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ViewComplaintActivity.this, ViewUserActivity.class)
                            .putExtra("userId", complaint.getUser().getId()));
                }
            });
        }
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBookmarked()) {
                    rotate(view, true);
                    floatingActionButton.setImageResource(R.drawable.ic_bookmark_on);
                    setIsBookmarked(true);
                } else {
                    rotate(view, false);
                    floatingActionButton.setImageResource(R.drawable.ic_bookmark);
                    setIsBookmarked(false);
                }
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivityContext, ViewCommentsActivity.class);
                intent.putExtra("complaintId", complaint.getId());
                startActivity(intent);
            }
        });
    }
}