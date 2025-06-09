package com.example.uber;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapSelectorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng destinoSeleccionado;
    private Button btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selector);

        btnConfirmar = findViewById(R.id.btnConfirmarDestino);
        btnConfirmar.setEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        btnConfirmar.setOnClickListener(v -> {
            if (destinoSeleccionado != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", destinoSeleccionado.latitude);
                resultIntent.putExtra("lng", destinoSeleccionado.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            destinoSeleccionado = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Destino seleccionado"));
            btnConfirmar.setEnabled(true);
        });

        // Centrar mapa en una ubicación general (puedes ajustar a tu zona)
        LatLng centro = new LatLng(19.4326, -99.1332); // Ciudad de México por defecto
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 12f));
    }
}
