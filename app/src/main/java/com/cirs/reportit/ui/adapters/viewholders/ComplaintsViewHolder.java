package com.cirs.reportit.ui.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.entities.Upvote;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.ui.activities.ViewCommentsActivity;
import com.cirs.reportit.ui.activities.ViewComplaintActivity;
import com.cirs.reportit.ui.adapters.ComplaintsAdapter;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.List;

public class ComplaintsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private NetworkImageView imgComplaint;

    private TextView txtTitle, txtDescription, txtTimeStamp, txtStatus;

    private Button btnComment, btnUpvote;

    private ImageButton imgbtnBookmark;

    private CardView cardView;

    private Complaint complaint;

    private Context context;

    private ComplaintsAdapter adapter;

    public ComplaintsViewHolder(View itemView, ComplaintsAdapter adapter) {
        super(itemView);
        this.cardView = (CardView) itemView;
        this.cardView.setOnClickListener(this);
        this.imgComplaint = (NetworkImageView) itemView.findViewById(R.id.img_complaint);
        this.txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        this.txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
        this.txtTimeStamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
        this.txtStatus = (TextView) itemView.findViewById(R.id.txt_status);
        this.btnUpvote = (Button) itemView.findViewById(R.id.btn_upvote);
        this.btnComment = (Button) itemView.findViewById(R.id.btn_comment);
        this.imgbtnBookmark = (ImageButton) itemView.findViewById(R.id.imgbtn_bookmark);
        this.btnComment.setOnClickListener(this);
        this.btnUpvote.setOnClickListener(this);
        this.imgbtnBookmark.setOnClickListener(this);
        this.adapter = adapter;
    }

    public void setFields(Complaint complaint, Context context) {
        this.complaint = complaint;
        this.context = context;
        this.txtTitle.setText(complaint.getTitle());
        this.txtDescription.setText(complaint.getDescription());
        this.txtTimeStamp.setText(complaint.getTimestamp().toString());
        setStatus(complaint.getStatus());
        this.btnUpvote.setText(complaint.getUpvotes() + "");
        this.btnComment.setText(complaint.getCommentCount() + "");
        if (complaint.isBookmarked() || new QueryHelper(context).isBookmarked(complaint)) {
            this.imgbtnBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_bookmark_on));
        } else {
            this.imgbtnBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_bookmark_off));
        }
        if (ReportItApplication.checkIfComplaintIsUpvoted(complaint.getId())) {
            this.btnUpvote.setCompoundDrawablesWithIntrinsicBounds(context.getResources()
                    .getDrawable(R.drawable.ic_action_upvote_on), null, null, null);
        } else {
            this.btnUpvote.setCompoundDrawablesWithIntrinsicBounds(context.getResources()
                    .getDrawable(R.drawable.ic_action_upvote_off), null, null, null);
        }

        String URL = Generator.getURLtoGetComplaintImage(complaint.getId());
        ImageLoader imageLoader = VolleyImageRequest.getInstance(context).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                this.imgComplaint, R.mipmap.ic_loading, R.mipmap.ic_no_connection));
        imgComplaint.setImageUrl(URL, imageLoader);
    }

    @Override
    public void onClick(View view) {
        if (view == cardView) {
            Intent intent = new Intent(context, ViewComplaintActivity.class);
            intent.putExtra("complaintId", complaint.getId());
            context.startActivity(intent);
        } else if (view.getId() == R.id.btn_comment) {
            Intent intent = new Intent(context, ViewCommentsActivity.class);
            intent.putExtra("complaintId", complaint.getId());
            context.startActivity(intent);
        } else if (view.getId() == R.id.imgbtn_bookmark) {
            if (complaint.isBookmarked() || new QueryHelper(context).isBookmarked(complaint)) {
                Toast.makeText(context, "You unbookmarked this complaint", Toast.LENGTH_SHORT).show();
                imgbtnBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_bookmark_off));
                complaint.setBookmarked(false);
                new QueryHelper(context).insertOrUpdateComplaint(complaint);
            } else {
                Toast.makeText(context, "You bookmarked this complaint", Toast.LENGTH_SHORT).show();
                imgbtnBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_bookmark_on));
                complaint.setBookmarked(true);
                new QueryHelper(context).insertOrUpdateComplaint(complaint);
            }
        } else if (view.getId() == R.id.btn_upvote &&
                !ReportItApplication.checkIfComplaintIsUpvoted(complaint.getId())) {
            Upvote upvote = new Upvote();
            upvote.setUser(complaint.getUser());
            upvote.setComplaint(complaint);
            new VolleyRequest<UpvoteResponse>(context).makeGsonRequest(
                    Request.Method.PUT,
                    Generator.getURLtoUpvote(),
                    new Upvote[]{upvote},
                    new Response.Listener<UpvoteResponse>() {
                        @Override
                        public void onResponse(UpvoteResponse response) {
                            ReportItApplication.addIdToUpvotedSet(complaint.getId());
                            Toast.makeText(context, "You upvoted this complaint", Toast.LENGTH_SHORT).show();
                            btnUpvote.setCompoundDrawablesWithIntrinsicBounds(context.getResources()
                                    .getDrawable(R.drawable.ic_action_upvote_on), null, null, null);
                            complaint.setUpvoted(true);
                            new QueryHelper(context).insertOrUpdateComplaint(complaint);
                            adapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "This complaint could not be upvoted", Toast.LENGTH_SHORT).show();
                        }
                    },
                    UpvoteResponse.class);
        }
    }

    private static class UpvoteResponse {
        private int created;
        private List<Upvote> failures;
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
        this.txtStatus.setBackgroundResource(resId);
    }
}
