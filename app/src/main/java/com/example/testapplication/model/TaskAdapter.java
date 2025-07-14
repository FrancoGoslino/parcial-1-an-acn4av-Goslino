package com.example.testapplication.model;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        TextView tituloText, descripcionText, fechaText, diasText;
        ImageButton editarButton, eliminarButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tituloText = itemView.findViewById(R.id.tituloTarea);
            descripcionText = itemView.findViewById(R.id.descripcionTarea);
            fechaText = itemView.findViewById(R.id.fechaTarea);
            diasText = itemView.findViewById(R.id.diasTarea);
            editarButton = itemView.findViewById(R.id.editarButton);
            eliminarButton = itemView.findViewById(R.id.eliminarButton);
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

        //  Eliminar con confirmación
        holder.eliminarButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar tarea")
                    .setMessage("¿Estás seguro de que querés borrar esta tarea?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && task.getId() != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("usuarios")
                                    .document(user.getUid())
                                    .collection("tareas")
                                    .document(task.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        taskList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(v.getContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(v.getContext(), "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(v.getContext(), "No se pudo identificar la tarea", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // 📝 Preparado para función de editar
        holder.editarButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Función de edición pendiente ✏️", Toast.LENGTH_SHORT).show();
            // Acá podrías abrir un fragment con los datos precargados
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}