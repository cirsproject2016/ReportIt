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
import com.cirs.reportit.ui.adapters.viewholders.ComplaintsViewHolder;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.zip.Inflater;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsViewHolder> {

    Complaint[] complaints;

    Context context;

    public ComplaintsAdapter(Complaint[] complaints, Context context) {
        this.complaints = complaints;
        this.context = context;
    }

    @Override
    public ComplaintsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_complaint_card_view, parent, false);
        return new ComplaintsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ComplaintsViewHolder holder, int position) {
        holder.setFields(complaints[position], context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return complaints.length;
    }
}
