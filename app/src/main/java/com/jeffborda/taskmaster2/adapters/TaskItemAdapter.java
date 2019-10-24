package com.jeffborda.taskmaster2.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskItemAdapter extends RecyclerView.Adapter {

    // The ViewHolder class holds on to the Fragment that we have created
    // Holds on to view data that we need
    // This is just an inner class, could be put in its own file
    public static class TaskItemViewHolder extends RecyclerView.ViewHolder {

        public TaskItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // RecyclerView needs us to create a new row, from scratch, for holding data

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    // RecyclerView has a row (maybe previously used) that needs to be updated for a particular location/index
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    // TODO: Change this with to return list.size()
    @Override
    public int getItemCount() {
        return 3;
    }
}
