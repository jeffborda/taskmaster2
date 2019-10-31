package com.jeffborda.taskmaster2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffborda.taskmaster2.R;
import com.jeffborda.taskmaster2.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder> {

    public List<Task> tasks;
    private OnTaskSelectedListener listener;

    public TaskItemAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    // Can use when a task needs to be added to RecyclerView
    public void addItem(Task task) {
        this.tasks.add(0,task);
        this.notifyItemInserted(0);
    }

    // The ViewHolder class holds on to the Fragment that we have created
    // Holds on to view data that we need
    // This is just an inner class, could be put in its own file
    public static class TaskItemViewHolder extends RecyclerView.ViewHolder {

        Task task;
        TextView taskTitleView;
        TextView taskDescriptionView;

        // The View that is taken in here is a TaskItem Fragment
        public TaskItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Set the text/values of all the views in the fragment
            this.taskTitleView = itemView.findViewById(R.id.fragment_task_item_title);
            this.taskDescriptionView = itemView.findViewById(R.id.fragment_task_item_description);
        }
    }


    // RecyclerView needs us to create a new row, from scratch, for holding data
    @NonNull
    @Override
    public TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reference the fragment that will be used
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_item, parent, false);
        final TaskItemViewHolder holder = new TaskItemViewHolder(view);
        // Do this to attach onClickListener to each fragment in RecyclerView
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTaskSelected(holder.task);
            }
        });
        return holder;
    }

    // RecyclerView has a row (maybe previously used) that needs to be updated for a particular location/index
    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        Task taskAtPosition = this.tasks.get(position);
        // Set the reference of which Task
        holder.task = taskAtPosition;
        holder.taskTitleView.setText(taskAtPosition.getTitle());
        holder.taskDescriptionView.setText(taskAtPosition.getDescription());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }
}
