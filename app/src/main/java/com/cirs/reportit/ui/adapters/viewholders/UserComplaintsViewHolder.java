package com.cirs.reportit.ui.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.activities.ViewComplaintActivity;

public class UserComplaintsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView txtComplaintTitle;

    private Complaint complaint;

    private Context context;

    public UserComplaintsViewHolder(View itemView) {
        super(itemView);
        txtComplaintTitle = (TextView) itemView;
        txtComplaintTitle.setOnClickListener(this);
    }

    public void setTxtComplaintTitle(Complaint complaint, Context context) {
        this.complaint = complaint;
        this.context = context;
        txtComplaintTitle.setText(Html.fromHtml("<u>" + this.complaint.getTitle() + "</u>"));
    }

    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, ViewComplaintActivity.class).putExtra("complaintId", complaint.getId()));
    }
}
