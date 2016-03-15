package com.cirs.reportit.ui.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Comment;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.ui.activities.ViewUserActivity;
import com.cirs.reportit.utils.CircularNetworkImageView;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;

public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private CircularNetworkImageView imgUser;

    private TextView txtName, txtComment, txtTimestamp;

    private Context context;

    private long userId;

    private CIRSUser user;

    public CommentsViewHolder(View itemView) {
        super(itemView);
        imgUser = (CircularNetworkImageView) itemView.findViewById(R.id.img_user);
        txtName = (TextView) itemView.findViewById(R.id.txt_name);
        txtComment = (TextView) itemView.findViewById(R.id.txt_comment);
        txtTimestamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
        txtName.setOnClickListener(this);
        imgUser.setOnClickListener(this);
    }

    public void setFields(Comment comment, Context context) {
        this.context = context;
        user = comment.getUser();
        if (user == null) {
            txtName.setText("Admin");
            userId = -1;
        } else if (user.getId() == ReportItApplication.getCirsUser().getId()) {
            txtName.setText("You");
            userId = user.getId();
        } else {
            txtName.setText(user.toString());
            userId = user.getId();
        }
        txtComment.setText(comment.getData());
        txtTimestamp.setText(comment.getTime().toString());

        String URL = Generator.getURLtoGetUserImage(userId);
        ImageLoader imageLoader = VolleyImageRequest.getInstance(context).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                imgUser, R.drawable.ic_my_profile, R.drawable.ic_my_profile));
        imgUser.setImageUrl(URL, imageLoader);
    }

    @Override
    public void onClick(View view) {
        if (userId > -1 && userId != ReportItApplication.getCirsUser().getId())
            context.startActivity(new Intent(context, ViewUserActivity.class).putExtra("userId", userId));
    }
}
