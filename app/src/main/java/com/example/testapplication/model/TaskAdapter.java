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
        TextView diasText;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tituloText = itemView.findViewById(R.id.tituloTarea);
            descripcionText = itemView.findViewById(R.id.descripcionTarea);
            fechaText = itemView.findViewById(R.id.fechaTarea);
            diasText = itemView.findViewById(R.id.diasTarea);

            if (diasText == null) {
                throw new RuntimeException("ERROR: No se pudo encontrar diasTarea en el layout.");
            }
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

        holder.tituloText.setText(task.getTitulo());
        holder.descripcionText.setText(task.getDescripcion());
        holder.fechaText.setText(dateFormat.format(task.getFecha()));

        List<String> dias = task.getDiasRepeticion();
        if (dias != null && !dias.isEmpty()) {
            String diasStr = "Se repite: " + String.join(", ", dias);
            holder.diasText.setText(diasStr);
        } else {
            holder.diasText.setText("Sin repetición");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}