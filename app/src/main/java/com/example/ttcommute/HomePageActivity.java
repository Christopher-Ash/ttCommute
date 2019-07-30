package com.example.ttcommute;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;


import android.os.Bundle;


import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;



import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.util.ArrayList;
import java.util.List;



/**
 * Restrict the map camera to certain bounds.
 */
public class HomePageActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final LatLng BOUND_CORNER_NW = new LatLng(10.6, -60.9); //latitude is bottom longitude is right
    private static final LatLng BOUND_CORNER_SE = new LatLng(10.9, -61.55); //lattitude is top and longitude is bottom
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();
    private MapView mapView;
    private Button logout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiY2hyaXMtNCIsImEiOiJjanhuejl5ZjcwMWs1M2JyeDBseHo0aDg0In0.4WvPzipWFjZ7cLlXE3r2Mg");
        setContentView(R.layout.activity_home_page);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomePageActivity.this, MainActivity.class));
                }
            }
        };

        logout = (Button) findViewById(R.id.button_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) { // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                //Set the boundary area for the map camera
                mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

                //Set the minimum zoom level of the map camera
                mapboxMap.setMinZoomPreference(2);


            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

}