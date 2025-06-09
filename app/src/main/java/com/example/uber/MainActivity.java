package com.example.uber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private Button btnLogin, btnRegister, btnConductor, btnPasajero;
    private TextView tvSeleccionRol;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnConductor = findViewById(R.id.btnConductor);
        btnPasajero = findViewById(R.id.btnPasajero);
        tvSeleccionRol = findViewById(R.id.tvSeleccionRol);

        mAuth = FirebaseAuth.getInstance();

        // Ocultar botones de rol inicialmente
        btnConductor.setVisibility(View.GONE);
        btnPasajero.setVisibility(View.GONE);
        tvSeleccionRol.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String pass = etPassword.getText().toString();
            login(correo, pass);
        });

        btnRegister.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String pass = etPassword.getText().toString();
            register(correo, pass);
        });

        btnConductor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConductorActivity.class);
            startActivity(intent);
        });

        btnPasajero.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PasajeroActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();

                        btnConductor.setVisibility(View.VISIBLE);
                        btnPasajero.setVisibility(View.VISIBLE);
                        tvSeleccionRol.setVisibility(View.VISIBLE);

                        etCorreo.setEnabled(false);
                        etPassword.setEnabled(false);
                        btnLogin.setEnabled(false);
                        btnRegister.setEnabled(false);

                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void register(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa el correo y la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
