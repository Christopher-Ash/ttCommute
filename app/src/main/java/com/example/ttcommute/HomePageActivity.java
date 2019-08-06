package com.example.ttcommute;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomePageActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Create a LatLngBounds that includes the country of Trinidad and Tobago.

    private LatLngBounds TrinidadTobago = new LatLngBounds(
            new LatLng(10.0470227, -61.9326551), new LatLng(11.346644, -60.4825221));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
}


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Constrain the camera target to the Adelaide bounds.
        mMap.setLatLngBoundsForCameraTarget(TrinidadTobago);

        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(9.5f);
        mMap.setMaxZoomPreference(16.0f);

        
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TrinidadTobago.getCenter(), 9));

        // Add a marker in Sydney and move the camera
        LatLng marker = new LatLng(10.6510516, -61.5101797);
        mMap.addMarker(new MarkerOptions().position(marker).title("Port of Spain"));
    }
}
