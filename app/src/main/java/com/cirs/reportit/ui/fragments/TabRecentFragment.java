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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ui.adapters.ComplaintsAdapter;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.Arrays;

public class TabRecentFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private Complaint[] complaints;

    private ProgressDialog progressDialog;

    public TabRecentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        View view = inflater.inflate(R.layout.fragment_tab_recent, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        new VolleyRequest<Complaint[]>(getActivity()).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoFetchAllComplaints(),
                null,
                new Response.Listener<Complaint[]>() {
                    @Override
                    public void onResponse(Complaint[] response) {
                        Arrays.sort(response);
                        complaints = response;
                        recyclerView.setAdapter(new ComplaintsAdapter(complaints, getActivity()));
                        System.out.println(complaints);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "There was an error loading complaints", Toast.LENGTH_LONG).show();
                    }
                },
                Complaint[].class);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }


}
