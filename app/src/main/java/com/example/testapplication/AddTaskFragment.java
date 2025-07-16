package com.example.testapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskFragment extends Fragment {
    private EditText inputTitulo, inputDescripcion;
    private TextView fechaSeleccionadaText;
    private Date fechaSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputTitulo = view.findViewById(R.id.inputTitulo);
        inputDescripcion = view.findViewById(R.id.inputDescripcion);
        fechaSeleccionadaText = view.findViewById(R.id.fechaSeleccionadaText);
        CheckBox checkLunes = view.findViewById(R.id.checkLunes);
        CheckBox checkMartes = view.findViewById(R.id.checkMartes);
        CheckBox checkMiercoles = view.findViewById(R.id.checkMiercoles);
        CheckBox checkJueves = view.findViewById(R.id.checkJueves);
        CheckBox checkViernes = view.findViewById(R.id.checkViernes);
        CheckBox checkSabado = view.findViewById(R.id.checkSabado);
        CheckBox checkDomingo = view.findViewById(R.id.checkDomingo);
        Button fechaButton = view.findViewById(R.id.selectFechaButton);
        Button guardarButton = view.findViewById(R.id.guardarTareaButton);

        // Selector de fecha
        fechaButton.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getContext(),
                    (dpView, y, m, d) -> {
                        cal.set(y, m, d);
                        fechaSeleccionada = cal.getTime();
                        fechaSeleccionadaText.setText("Fecha: " + d + "/" + (m+1) + "/" + y);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Guardar tarea
        guardarButton.setOnClickListener(v -> {
            String titulo = inputTitulo.getText().toString().trim();
            String desc  = inputDescripcion.getText().toString().trim();
            if (titulo.isEmpty() || desc.isEmpty() || fechaSeleccionada == null) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Recolectar días marcados justo al guardar
            List<String> diasSeleccionados = new ArrayList<>();
            if (checkLunes.isChecked())      diasSeleccionados.add("Lunes");
            if (checkMartes.isChecked())     diasSeleccionados.add("Martes");
            if (checkMiercoles.isChecked())  diasSeleccionados.add("Miércoles");
            if (checkJueves.isChecked())     diasSeleccionados.add("Jueves");
            if (checkViernes.isChecked())    diasSeleccionados.add("Viernes");
            if (checkSabado.isChecked())     diasSeleccionados.add("Sábado");
            if (checkDomingo.isChecked())    diasSeleccionados.add("Domingo");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> tareaMap = new HashMap<>();
            tareaMap.put("titulo", titulo);
            tareaMap.put("descripcion", desc);
            tareaMap.put("fecha", fechaSeleccionada);
            tareaMap.put("diasRepeticion", diasSeleccionados);
            tareaMap.put("timestamp", FieldValue.serverTimestamp());

            // Primero agrego la tarea, obtengo el ID, actualizo el doc con ese ID
            db.collection("usuarios")
                    .document(uid)
                    .collection("tareas")
                    .add(tareaMap)
                    .addOnSuccessListener(docRef -> {
                        String tareaId = docRef.getId();
                        tareaMap.put("id", tareaId);
                        docRef.set(tareaMap); // ahora el documento en Firestore incluye el campo id

                        // Armo el Bundle con todos los datos + el ID
                        Bundle resultado = new Bundle();
                        resultado.putString("id", tareaId);
                        resultado.putString("titulo", titulo);
                        resultado.putString("descripcion", desc);
                        resultado.putLong("fechaMillis", fechaSeleccionada.getTime());
                        resultado.putStringArrayList("dias",
                                new ArrayList<>(diasSeleccionados));

                        // Envío el resultado y vuelvo al fragment anterior
                        getParentFragmentManager()
                                .setFragmentResult("nuevaTarea", resultado);
                        FragmentTransaction tx = getParentFragmentManager().beginTransaction();
                        tx.replace(R.id.fragment, new FirstFragment());
                        tx.commit();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al guardar tarea", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}