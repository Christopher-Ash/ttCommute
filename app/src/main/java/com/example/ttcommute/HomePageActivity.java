package com.example.ttcommute;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.ZoomControls;

import java.io.IOException;
import java.util.List;


public class HomePageActivity extends FragmentActivity
        implements OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private GoogleMap mMap;

    //define for geolocation block. chk onCreate method
    Button geoLocationbtn;

    // Create a LatLngBounds that includes the country of Trinidad and Tobago.
    private LatLngBounds TrinidadTobago = new LatLngBounds(
            new LatLng(10.0470227, -61.9326551), new LatLng(11.346644, -60.4825221));

    Button clear;

    // Zoom btn controls
    ZoomControls zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Zoom control
        zoom = (ZoomControls) findViewById(R.id.zcZoom);
        //setting zoom -in and -out click listener
        //zoom out
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        //zoom in
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        // search bar finding locations --works correctly
        geoLocationbtn=(Button) findViewById(R.id.btSearch);
        geoLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchText = (EditText) findViewById(R.id.etLocationEntry);
                String search = searchText.getText().toString();
                if (search != null && !search.equals("")) {
                    //List<android.location.Address> addressList = null;
                    //structure to hold sec info
                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(HomePageActivity.this);
                    //pop address ,ist
                    try {
                        addressList = geocoder.getFromLocationName(search, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0); //first elemnt of addrlist
                    //lon lat from address abive
                    LatLng latLang = new LatLng(address.getLatitude(), address.getLongitude());
                    //provided by android add class btw
                    mMap.addMarker(new MarkerOptions().position(latLang).title("from geocoder"));
                    //move camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLang));
                }
            }
        });
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

        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        //block which places a marker based on user interaction
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("from onMapClick"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        });

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // Constrain the camera target to the Trinidad & Tobago bounds.
        mMap.setLatLngBoundsForCameraTarget(TrinidadTobago);

        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(9.5f);
        mMap.setMaxZoomPreference(16.0f);

        
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TrinidadTobago.getCenter(), 9));

        // Add a marker in Trinidad and move the camera
        LatLng marker = new LatLng(10.6510516, -61.5101797);
        mMap.addMarker(new MarkerOptions().position(marker).title("Port of Spain"));
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}
