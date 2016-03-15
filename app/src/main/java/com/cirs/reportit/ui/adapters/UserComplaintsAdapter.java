package com.cirs.reportit.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.adapters.viewholders.UserComplaintsViewHolder;

import java.util.ArrayList;

public class UserComplaintsAdapter extends RecyclerView.Adapter<UserComplaintsViewHolder> {

    private ArrayList<Complaint> complaints;

    private Context context;

    public UserComplaintsAdapter(ArrayList<Complaint> complaints, Context context) {
        this.complaints = complaints;
        this.context = context;
    }

    @Override
    public UserComplaintsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_user_complaint_textview, parent, false);
        return new UserComplaintsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserComplaintsViewHolder holder, int position) {
        holder.setTxtComplaintTitle(complaints.get(position), context);
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }
}
