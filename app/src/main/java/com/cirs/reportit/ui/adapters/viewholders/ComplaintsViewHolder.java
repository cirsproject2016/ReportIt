package com.cirs.reportit.ui.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.activities.ViewComplaintActivity;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;

public class ComplaintsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private NetworkImageView imgComplaint;

    private TextView txtTitle, txtDescription, txtTimeStamp, txtStatus, txtUpVotes, txtComments;

    private Button btnComments;

    private ToggleButton tglbtnUpVotes, tglbtnBookmark;

    private CardView cardView;

    private Complaint complaint;

    private Context context;

    public ComplaintsViewHolder(View itemView) {
        super(itemView);
        this.cardView = (CardView) itemView;
        this.cardView.setOnClickListener(this);
        this.imgComplaint = (NetworkImageView) itemView.findViewById(R.id.img_complaint);
        this.txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        this.txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
        this.txtTimeStamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
        this.txtStatus = (TextView) itemView.findViewById(R.id.txt_status);
        this.txtUpVotes = (TextView) itemView.findViewById(R.id.txt_upvotes);
        this.txtComments = (TextView) itemView.findViewById(R.id.txt_comments);
        this.btnComments = (Button) itemView.findViewById(R.id.btn_comment);
        this.tglbtnUpVotes = (ToggleButton) itemView.findViewById(R.id.toggle_btn_upvotes);
        this.tglbtnBookmark = (ToggleButton) itemView.findViewById(R.id.toggle_btn_bookmark);
    }

    public void setFields(Complaint complaint, Context context) {
        this.complaint = complaint;
        this.context = context;
        this.txtTitle.setText(complaint.getTitle());
        this.txtDescription.setText(complaint.getDescription());
        this.txtTimeStamp.setText(complaint.getTimestamp().toString());
        setStatus(complaint.getStatus());
        this.txtUpVotes.setText(complaint.getUpvotes() + "");
        this.txtComments.setText(complaint.getCommentsCount() + "");
        this.tglbtnBookmark.setChecked(complaint.isBookmarked());

        String URL = Generator.getURLtoGetComplaintImage(complaint.getId());
        ImageLoader imageLoader = VolleyImageRequest.getInstance(context).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                this.imgComplaint, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_dialog_alert));
        imgComplaint.setImageUrl(URL, imageLoader);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, ViewComplaintActivity.class);
        intent.putExtra("complaintId", complaint.getId());
        context.startActivity(intent);
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
            case "COMPLETE":
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
