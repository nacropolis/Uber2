package com.example.uber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConductorActivity extends AppCompatActivity {

    EditText etPasajeros, etCosto, etModelo, etColor, etCiudad, etHora, etLugaresPaso, etPlacas;
    Button btnCrearSala, btnIniciarViaje, btnSeleccionarDestino;
    TextView tvSalaCreada;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    LatLng destinoSeleccionado = null;
    String salaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor);

        etPasajeros = findViewById(R.id.etPasajeros);
        etCosto = findViewById(R.id.etCosto);
        etModelo = findViewById(R.id.etModelo);
        etColor = findViewById(R.id.etColor);
        etCiudad = findViewById(R.id.etCiudad);
        etHora = findViewById(R.id.etHora);
        etLugaresPaso = findViewById(R.id.etLugaresPaso);
        etPlacas = findViewById(R.id.etPlacas);
        btnCrearSala = findViewById(R.id.btnCrearSala);
        btnIniciarViaje = findViewById(R.id.btnIniciarViaje);
        btnSeleccionarDestino = findViewById(R.id.btnSeleccionarDestino);
        tvSalaCreada = findViewById(R.id.tvSalaCreada);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSeleccionarDestino.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapSelectorActivity.class);
            startActivityForResult(intent, 1);
        });

        btnCrearSala.setOnClickListener(v -> crearSala());

        btnIniciarViaje.setOnClickListener(v -> {
            if (salaId != null) {
                db.collection("salas").document(salaId)
                        .update("iniciado", true);
            }
        });
    }

    private void crearSala() {
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> sala = new HashMap<>();
        sala.put("conductorId", uid);
        sala.put("codigoConductor", mAuth.getCurrentUser().getEmail());
        sala.put("pasajerosMax", Integer.parseInt(etPasajeros.getText().toString()));
        sala.put("pasajerosActuales", 0);
        sala.put("costo", etCosto.getText().toString());
        sala.put("modelo", etModelo.getText().toString());
        sala.put("color", etColor.getText().toString());
        sala.put("placas", etPlacas.getText().toString());
        sala.put("ciudad", etCiudad.getText().toString());
        sala.put("hora", etHora.getText().toString());
        sala.put("lugaresPaso", etLugaresPaso.getText().toString());
        sala.put("iniciado", false);

        if (destinoSeleccionado != null) {
            sala.put("destinoLat", destinoSeleccionado.latitude);
            sala.put("destinoLng", destinoSeleccionado.longitude);
        }

        db.collection("salas").add(sala).addOnSuccessListener(docRef -> {
            salaId = docRef.getId();
            tvSalaCreada.setVisibility(View.VISIBLE);
            tvSalaCreada.setText("Sala creada: " + salaId);
            btnIniciarViaje.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            destinoSeleccionado = new LatLng(
                    data.getDoubleExtra("lat", 0),
                    data.getDoubleExtra("lng", 0)
            );
        }
    }
}
