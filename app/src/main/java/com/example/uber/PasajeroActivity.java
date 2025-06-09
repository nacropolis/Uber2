package com.example.uber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PasajeroActivity extends AppCompatActivity {

    private ListView listaSalas;
    private TextView textEsperando;
    private ArrayAdapter<String> adapter;
    private List<String> salasList = new ArrayList<>();
    private List<DocumentSnapshot> salasDocs = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);

        listaSalas = findViewById(R.id.listaSalas);
        textEsperando = findViewById(R.id.textEsperando);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, salasList);
        listaSalas.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarSalas();

        listaSalas.setOnItemClickListener((parent, view, position, id) -> {
            DocumentSnapshot sala = salasDocs.get(position);
            Long actuales = sala.getLong("pasajerosActuales");
            Long max = sala.getLong("pasajerosMax");

            if (actuales != null && max != null && actuales < max) {
                db.collection("salas").document(sala.getId())
                        .update("pasajerosActuales", actuales + 1);

                escucharInicioViaje(sala);
                String color = sala.getString("color");
                String placas = sala.getString("placas");

                Toast.makeText(this, "Te uniste a la sala. Esperando inicio...", Toast.LENGTH_LONG).show();

                // Mostrar información del vehículo mientras espera
                textEsperando.setText("Esperando que inicie el viaje...\nColor del vehículo: " + color + "\nPlacas: " + placas);
                listaSalas.setVisibility(View.GONE);
                textEsperando.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Sala llena", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSalas() {
        db.collection("salas")
                .whereEqualTo("iniciado", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (snapshots == null) return;

                    salasList.clear();
                    salasDocs.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Long actuales = doc.getLong("pasajerosActuales");
                        Long max = doc.getLong("pasajerosMax");

                        if (actuales != null && max != null && actuales < max) {
                            String conductor = doc.getString("codigoConductor");
                            salasList.add("Conductor: " + conductor + "\nCiudad: " + doc.getString("ciudad")+ "\nPlacas: " + doc.getString("placas")+ "\nColor: " + doc.getString("color"));
                            salasDocs.add(doc);
                        }
                    }

                    if (salasList.isEmpty()) {
                        listaSalas.setVisibility(View.GONE);
                        textEsperando.setText("No hay salas disponibles por ahora.");
                        textEsperando.setVisibility(View.VISIBLE);
                    } else {
                        listaSalas.setVisibility(View.VISIBLE);
                        textEsperando.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void escucharInicioViaje(DocumentSnapshot sala) {
        db.collection("salas").document(sala.getId())
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null && snapshot.exists() &&
                            Boolean.TRUE.equals(snapshot.getBoolean("iniciado"))) {

                        double lat = snapshot.getDouble("destinoLat");
                        double lng = snapshot.getDouble("destinoLng");

                        Intent intent = new Intent(this, MapaRutaPasajeroActivity.class);
                        intent.putExtra("destinoLat", lat);
                        intent.putExtra("destinoLng", lng);
                        startActivity(intent);
                    }
                });
    }
}
