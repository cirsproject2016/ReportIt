package com.cirs.reportit.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.activities.ViewComplaintActivity;
import com.cirs.reportit.ui.adapters.viewholders.BlankViewHolder;
import com.cirs.reportit.ui.adapters.viewholders.ComplaintsViewHolder;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.zip.Inflater;

public class ComplaintsAdapter extends RecyclerView.Adapter {

    private Complaint[] complaints;

    private Context context;

    private static final int TYPE_FOOTER = 100;

    public ComplaintsAdapter(Complaint[] complaints, Context context) {
        this.complaints = complaints;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_blank, parent, false);
            return new BlankViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_complaint_card_view, parent, false);
        return new ComplaintsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() != TYPE_FOOTER) {
            ComplaintsViewHolder complaintsViewHolder = (ComplaintsViewHolder) holder;
            complaintsViewHolder.setFields(complaints[position], this.context);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return complaints.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == complaints.length) return TYPE_FOOTER;
        return super.getItemViewType(position);
    }
}
