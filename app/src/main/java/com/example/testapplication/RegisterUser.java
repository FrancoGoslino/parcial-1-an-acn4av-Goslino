package com.example.testapplication;

import static android.content.ContentValues.TAG;

import static java.util.regex.Pattern.matches;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resgisterUser), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    public void ResgitroApp(View view) {
        EditText UserEditText = findViewById(R.id.editTextUserRegister);
        EditText emailEditText = findViewById(R.id.editTextEmailRegister);
        EditText passwordEditText = findViewById(R.id.editTextPasswordRegister);

        String user1 = UserEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validaciónes
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6 && !password.matches(".*[A-Z].*") && !password.matches(".*[a-z].*")) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres, una letra mayúscula y una letra minúscula", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!user1.matches("^[a-zA-Z]{4,}$")) {
                Toast.makeText(this, "El nombre de usuario debe tener al menos 4 letras sin números ni símbolos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user1.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

        // Registro del usuario  Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("nombreUsuario", user1);
                        datosUsuario.put("email", email);
                        datosUsuario.put("fechaRegistro", new Date());

                        db.collection("Usuarios").document(uid).set(datosUsuario)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario guardado"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error al guardar", e));

                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity2.class));
                    }
                });

    }

    public void OnClick_VolverAtras(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}