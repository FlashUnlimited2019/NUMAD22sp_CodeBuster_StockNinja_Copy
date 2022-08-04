package edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.R;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Discussion;

public class DiscussionRecyclerAdapter extends RecyclerView.Adapter<DiscussionRecyclerAdapter.DiscussionViewHolder> {
    private ArrayList<Discussion> discussionList;

    public DiscussionRecyclerAdapter(ArrayList<Discussion> discussionList) {
        this.discussionList = discussionList;
    }

    public class DiscussionViewHolder extends RecyclerView.ViewHolder {
        private TextView discussionTitle;
        private TextView discussionContent;
        private TextView discussionUser;
        private TextView discussionPlace;
        private TextView discussionTime;

        public DiscussionViewHolder(@NonNull View itemView) {
            super(itemView);
            discussionTitle = itemView.findViewById(R.id.discussion_title);
            discussionContent = itemView.findViewById(R.id.discussion_content);
            discussionUser = itemView.findViewById(R.id.discussion_user);
            discussionPlace = itemView.findViewById(R.id.discussion_place);
            discussionTime = itemView.findViewById(R.id.discussion_time);
        }
    }

    @NonNull
    @Override
    public DiscussionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.discussion_post_item, parent, false);
        return new DiscussionViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
        String title = discussionList.get(position).getTitle();
        String content = discussionList.get(position).getContent();
        String user = discussionList.get(position).getUsername();
        String place = discussionList.get(position).getPlace();
        String time = discussionList.get(position).getTimestamp().substring(0,16);
        holder.discussionTitle.setText(title);
        holder.discussionContent.setText(content);
        holder.discussionUser.setText(user);
        holder.discussionPlace.setText(place);
        holder.discussionTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }
}
