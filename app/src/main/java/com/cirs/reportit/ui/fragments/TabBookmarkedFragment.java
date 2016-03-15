package com.cirs.reportit.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.ui.adapters.ComplaintsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabBookmarkedFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    public TabBookmarkedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = ProgressDialog.show(getActivity(), "Loading", "", true);
        View view = inflater.inflate(R.layout.fragment_tab_bookmarked, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Complaint> complaintsList = new ArrayList<>(new QueryHelper(getActivity())
                .getBookmarkedComplaints());
        Collections.sort(complaintsList);
        recyclerView.setAdapter(new ComplaintsAdapter(complaintsList.toArray(new Complaint[]{}), getActivity()));
        progressDialog.dismiss();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<Complaint> complaintsList = new ArrayList<>(new QueryHelper(getActivity())
                        .getBookmarkedComplaints());
                Collections.sort(complaintsList);
                recyclerView.setAdapter(new ComplaintsAdapter(complaintsList.toArray(new Complaint[]{}), getActivity()));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

}
