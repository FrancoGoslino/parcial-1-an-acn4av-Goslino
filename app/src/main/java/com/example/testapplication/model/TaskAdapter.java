package com.example.testapplication.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tituloText;
        TextView descripcionText;
        TextView fechaText;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tituloText = itemView.findViewById(R.id.tituloTarea);
            descripcionText = itemView.findViewById(R.id.descripcionTarea);
            fechaText = itemView.findViewById(R.id.fechaTarea);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tituloText.setText(task.gettitulo());
        holder.descripcionText.setText(task.getDescripcion());
        holder.fechaText.setText(dateFormat.format(task.getFecha()));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}