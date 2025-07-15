package com.example.testapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SecondFragment extends Fragment {

    private CalendarView calendarView;
    private TextView emocionText;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(android.view.LayoutInflater inflater,
                             android.view.ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        calendarView = view.findViewById(R.id.calendarView);
        emocionText  = view.findViewById(R.id.emocionText);

        db   = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Día tocado
        calendarView.setOnDateChangeListener((cv, year, month, day) -> {
            String fechaStr = String.format("%02d-%02d-%04d", day, month + 1, year);
            emocionText.setText("📅 Día seleccionado: " + fechaStr);
            mostrarDialogoEmocional(fechaStr);
        });
    }

    private void mostrarDialogoEmocional(String fechaStr) {
        String[] emociones = {"Muy bueno", "Bueno", "Regular", "Malo", "Muy malo"};

        new AlertDialog.Builder(getContext())
                .setTitle("¿Cómo te sentiste ese día?")
                .setSingleChoiceItems(emociones, -1, (dialog, which) -> {
                    String emocionSeleccionada = emociones[which];
                    guardarEmocion(fechaStr, emocionSeleccionada);
                    emocionText.setText("📅 " + fechaStr + " — " + emocionSeleccionada);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarEmocion(String fechaStr, String emocion) {
        Map<String, Object> data = new HashMap<>();
        data.put("emocion", emocion);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("usuarios")
                .document(user.getUid())
                .collection("emociones")
                .document(fechaStr)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Emoción guardada", Toast.LENGTH_SHORT).show();
                    pintarColorDelDia(fechaStr, emocion);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar emoción", Toast.LENGTH_SHORT).show();
                });
    }

    private void pintarColorDelDia(String fechaStr, String emocion) {
        Date date;
        try {
            date = formatoFecha.parse(fechaStr);
        } catch (Exception e) {
            return;
        }

        long millis = date.getTime();
        int color = obtenerColorPorEmocion(emocion);
        calendarView.setDate(millis, false, true);
        calendarView.setBackgroundColor(color);
    }

    private int obtenerColorPorEmocion(String emocion) {
        switch (emocion) {
            case "Muy bueno": return Color.parseColor("#4CAF50"); // verde
            case "Bueno":     return Color.parseColor("#FFEB3B"); // amarillo
            case "Regular":   return Color.parseColor("#BDBDBD"); // gris
            case "Malo":      return Color.parseColor("#FF9800"); // naranja
            case "Muy malo":  return Color.parseColor("#F44336"); // rojo
            default:          return Color.WHITE;
        }
    }
}