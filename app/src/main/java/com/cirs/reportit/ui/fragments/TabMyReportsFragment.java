package com.cirs.reportit.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.ui.adapters.ComplaintsAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class TabMyReportsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private ArrayList<Complaint> complaints;

    ProgressDialog progressDialog;

    public TabMyReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = ProgressDialog.show(getActivity(), "Loading", "", true);
        View view = inflater.inflate(R.layout.fragment_tab_my_reports, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ReportItApplication.fetchAllComplaintsByThisUser();
                complaints = new ArrayList<>(new QueryHelper(getActivity()).getComplaintWithoutComment(ReportItApplication.getCirsUser().getId()));
                Complaint[] complaintArray = new Complaint[complaints.size()];
                for (int i = 0; i < complaints.size(); i++) {
                    complaintArray[i] = complaints.get(i);
                }
                Arrays.sort(complaintArray);
                recyclerView.setAdapter(new ComplaintsAdapter(complaintArray, getActivity()));
                progressDialog.dismiss();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReportItApplication.fetchAllComplaintsByThisUser();
                complaints = new ArrayList<>(new QueryHelper(getActivity()).getComplaintWithoutComment(ReportItApplication.getCirsUser().getId()));
                Complaint[] complaintArray = new Complaint[complaints.size()];
                for (int i = 0; i < complaints.size(); i++) {
                    complaintArray[i] = complaints.get(i);
                }
                Arrays.sort(complaintArray);
                recyclerView.setAdapter(new ComplaintsAdapter(complaintArray, getActivity()));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }
}
