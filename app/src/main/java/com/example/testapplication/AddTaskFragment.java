package com.example.testapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTaskFragment extends Fragment {
    private EditText inputTitulo, inputDescripcion;
    private TextView fechaSeleccionadaText;
    private Date fechaSeleccionada;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTaskFragment newInstance(String param1, String param2) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // botón de guardar
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddTaskFragment", "Botón GUARDAR fue presionado");
                String titulo = inputTitulo.getText().toString().trim();
                String descripcion = inputDescripcion.getText().toString().trim();

                if (titulo.isEmpty() || descripcion.isEmpty() || fechaSeleccionada == null) {
                    Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> diasSeleccionados = new ArrayList<>();
                if (checkLunes.isChecked()) diasSeleccionados.add("Lunes");
                if (checkMartes.isChecked()) diasSeleccionados.add("Martes");
                if (checkMiercoles.isChecked()) diasSeleccionados.add("Miércoles");
                if (checkJueves.isChecked()) diasSeleccionados.add("Jueves");
                if (checkViernes.isChecked()) diasSeleccionados.add("Viernes");
                if (checkSabado.isChecked()) diasSeleccionados.add("Sábado");
                if (checkDomingo.isChecked()) diasSeleccionados.add("Domingo");

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> tarea = new HashMap<>();
                    tarea.put("titulo", titulo);
                    tarea.put("descripcion", descripcion);
                    tarea.put("fecha", fechaSeleccionada);
                    tarea.put("diasRepeticion", diasSeleccionados);
                    tarea.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("usuarios")
                            .document(uid)
                            .collection("tareas")
                            .add(tarea)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getContext(), "Tarea guardada con éxito", Toast.LENGTH_SHORT).show();
                                irAFirstFragment();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al guardar tarea", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        // botón seleccionar fecha
        fechaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int year = calendario.get(Calendar.YEAR);
                int month = calendario.get(Calendar.MONTH);
                int day = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view1, y, m, d) -> {
                    Calendar seleccion = Calendar.getInstance();
                    seleccion.set(y, m, d);
                    fechaSeleccionada = seleccion.getTime();
                    fechaSeleccionadaText.setText("Fecha: " + d + "/" + (m + 1) + "/" + y);
                }, year, month, day);
                datePicker.show();
            }
        });
    }

    private void irAFirstFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.firstFragment, new FirstFragment());
        transaction.commit();
    }
}