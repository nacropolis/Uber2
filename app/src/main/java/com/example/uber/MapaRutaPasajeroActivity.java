package com.example.uber;

import android.Manifest; // Import the Manifest class
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapaRutaPasajeroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double destinoLat, destinoLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_ruta_pasajero);

        destinoLat = getIntent().getDoubleExtra("destinoLat", 0);
        destinoLng = getIntent().getDoubleExtra("destinoLng", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destino = new LatLng(destinoLat, destinoLng);
        mMap.addMarker(new MarkerOptions().position(destino).title("Destino final"));

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101); // Correct way to reference the permission
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng origen = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(origen).title("Tú"));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 12f));
                mMap.addPolyline(new PolylineOptions()
                        .add(origen, destino)
                        .color(Color.BLUE)
                        .width(8f));
            }
        });
    }
}