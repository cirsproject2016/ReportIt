package com.cirs.reportit.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cirs.R;
import com.cirs.entities.Comment;
import com.cirs.reportit.ui.adapters.viewholders.BlankViewHolder;
import com.cirs.reportit.ui.adapters.viewholders.CommentsViewHolder;

/**
 * Created by Kshitij on 09-03-2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter {

    private Comment[] comments;

    private Context context;

    public CommentsAdapter(Comment[] comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (comments.length == 0) {
            return new BlankViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.element_no_comments, parent, false));
        }
        return new CommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.element_comment_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (comments.length > 0) {
            ((CommentsViewHolder) holder).setFields(comments[position], context);
        }
    }

    @Override
    public int getItemCount() {
        return comments.length == 0 ? 1 : comments.length;
    }
}
