package com.cirs.reportit.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Comment;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.ui.adapters.CommentsAdapter;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.sql.Timestamp;
import java.util.Arrays;

public class ViewCommentsActivity extends AppCompatActivity {

    public static final String EXTRA_COMPLAINT_ID = "complaintId";

    private RecyclerView recyclerView;

    private EditText edtComment;

    private ImageButton imgbtnComment;

    private Context mActivityContext = this;

    private Complaint complaint;

    private Comment[] comments;

    private ProgressDialog progressDialog;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
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
                        progressDialog.dismiss();
                        complaint = response;
                        comments = complaint.getComments().toArray(new Comment[]{});
                        Arrays.sort(comments);
                        initializeViews();
                        setListeners();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(mActivityContext, "There was an error loading comments", Toast.LENGTH_LONG).show();
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

    private void initializeViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        edtComment = (EditText) findViewById(R.id.edt_comment);
        imgbtnComment = (ImageButton) findViewById(R.id.imgbtn_comment);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivityContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new CommentsAdapter(comments, mActivityContext));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        imgbtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtComment.getText())) {
                    Toast.makeText(mActivityContext, "Please enter a valid comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                Comment comment = new Comment();
                comment.setData(edtComment.getText().toString());
                CIRSUser newUser = new CIRSUser();
                newUser.setId(ReportItApplication.getCirsUser().getId());
                comment.setUser(newUser);
                Complaint newComplaint = new Complaint();
                newComplaint.setId(complaint.getId());
                comment.setComplaint(newComplaint);
                comment.setTime(new Timestamp(System.currentTimeMillis()));
                new VolleyRequest<Comment>(mActivityContext).makeGsonRequest(
                        Request.Method.PUT,
                        Generator.getURLtoSendComment(),
                        comment,
                        new Response.Listener<Comment>() {
                            @Override
                            public void onResponse(Comment response) {
                                progressDialog.dismiss();
                                Toast.makeText(mActivityContext, "Comment successfully submitted", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                error.printStackTrace();
                                Toast.makeText(mActivityContext, "There was an error submitting comment", Toast.LENGTH_LONG).show();
                            }
                        },
                        Comment.class);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new VolleyRequest<Complaint>(mActivityContext).makeGsonRequest(
                        Request.Method.GET,
                        Generator.getURLtoGetComplaintById(getIntent().getLongExtra(EXTRA_COMPLAINT_ID, -1)),
                        null,
                        new Response.Listener<Complaint>() {
                            @Override
                            public void onResponse(Complaint response) {
                                complaint = response;
                                comments = complaint.getComments().toArray(new Comment[]{});
                                Arrays.sort(comments);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                swipeRefreshLayout.setRefreshing(false);
                                error.printStackTrace();
                                Toast.makeText(mActivityContext, "There was an error loading comments", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        },
                        Complaint.class);
            }
        });
    }
}
