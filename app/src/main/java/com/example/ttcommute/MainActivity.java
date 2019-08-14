package com.example.ttcommute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.widget.CheckBox;
import android.widget.Toast;

// Map initialization classes
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

// Location component classes
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;

// Marker addition classes
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.api.geocoding.v5.MapboxGeocoding.*;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// Route calculation classes
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

//classes needed for map restriction
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;


// Navigation UI classes
import android.view.View;
import android.widget.Button;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener,
        PermissionsListener{
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private FloatingActionButton button;
    //variables for search
    private FloatingActionButton search;
    private static final int REQUEST_CODE_AUTOCOMPLETE =1;
    public static final int MODE_FULLSCREEN = 1;
    public int backgrounColor() {
        return 0;
    }
    //variable to filter country in search
    private List<String> countries = new ArrayList<>();
    //place picker variable
    private static final int PLACE_SELECTION_REQUEST_CODE = 56789;
    //taxi stand locator variables
    private FeatureCollection featureCollection;
    private static final String PROPERTY_SELECTED = "selected";
    //filter display variables
    private CheckBox showTaxi;            //chip to show taxis
    private CheckBox showServices;        //chip to show essential serices


    //variables for map restriction
    private static final LatLng BOUND_CORNER_NW = new LatLng(10, -60); //latitude is bottom longitude is right
    private static final LatLng BOUND_CORNER_SE = new LatLng(11, -62); //lattitude is top and longitude is bottom
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        //Set the boundary area for the map area
        mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

        //Set the minimum zoom level of the map camera
        mapboxMap.setMinZoomPreference(8);

        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);

                //layer chips setup
                showTaxi =  findViewById(R.id.checkBox);
                showServices = findViewById(R.id.checkBox2);



                mapboxMap.addOnMapClickListener(MainActivity.this);

                showTaxi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox) view).isChecked();
                        if (checked)
                            addTaxiIconSymbolLayer(style);

                        else

                            //remove layer
                            removeTaxiIconLayer(style);
                    }
                });

                showServices.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean checked = ((CheckBox) view).isChecked();
                                if (checked) {
                                    addHospitalIconSymbolLayer(style);
                                    addPoliceIconSymbolLayer(style);
                                } else {
                                    //remove layers
                                    removeHospitalIconLayer(style);
                                    removePoliceIconLayer(style);
                                }
                            }

                } );

                search = findViewById(R.id.searchbutton);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchActivity();
                    }
                });

                button = findViewById(R.id.navbutton);
                button.setEnabled(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean simulateRoute = false;
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(simulateRoute)
                                .build();
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(MainActivity.this, options);

                    }
                });
            }
        });
        //calling method to show markers


        /*

        //deals with how selected icon is presented

        initSelectedTaxiSymbolLayer();

        // Create a list of features from the feature collection
        List<Feature> featureList = featureCollection.features();

        // Retrieve and update the source designated for showing the store location icons
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("store-location-source-id");
        if (source != null) {
            source.setGeoJson(FeatureCollection.fromFeatures(featureList));
        }

        if (featureList != null) {

            for (int x = 0; x < featureList.size(); x++) {

                Feature singleLocation = featureList.get(x);

                // Get the single location's String properties to place in its map marker
                String singleLocationName = singleLocation.getStringProperty("name");
                String singleLocationHours = singleLocation.getStringProperty("hours");
                String singleLocationDescription = singleLocation.getStringProperty("description");
                String singleLocationPhoneNum = singleLocation.getStringProperty("phone");


                // Add a boolean property to use for adjusting the icon of the selected store location
                singleLocation.addBooleanProperty(PROPERTY_SELECTED, false);

                // Get the single location's LatLng coordinates
                Point singleLocationPosition = (Point) singleLocation.geometry();

                // Create a new LatLng object with the Position object created above
                LatLng singleLocationLatLng = new LatLng(singleLocationPosition.latitude(),
                        singleLocationPosition.longitude());

            }
        }*/
    }

    //checkbox method


    //method to add destination layer
    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    //method to drop marker and draw route when map is clicked
    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Intent intent = new PlacePicker.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(
                        PlacePickerOptions.builder()
                                .statingCameraPosition(
                                        new CameraPosition.Builder()
                                                .target(point)
                                                .zoom(16)
                                                .build())
                                .build())
                .build(this);
        startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);



        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());


        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);
        button.setBackgroundResource(R.color.mapboxBlue);
        return true;
    }

    //method to calculate route between user location and marker
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            //deprecated function
                            //navigationMapRoute.removeRoute();
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                            navigationMapRoute.updateRouteVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    //method to enable the location component. This enables the user's current location to be seen on th map via a puck
    //It checks user permissions with respect to the access of user location as well
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            //deprecated function commented out
            //locationComponent.activateLocationComponent(this, loadedMapStyle);
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .useDefaultLocationEngine(true)
                    .build();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }



    //method enabling search with filter
    private void searchActivity(){

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        //restricting search to Trinidad
                        .country("TT")
                        .backgroundColor(Color.parseColor("#1094A5"))
                        .build(PlaceOptions.MODE_FULLSCREEN)

                )
                .build(this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        //option to clear recent history
        PlaceAutocomplete.clearRecentHistory(this);

    }



    //activity result for location search and picker

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //search activity
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();
            geoCoder(feature.text());

        } else {
            //location picker activity
            if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {

                // Retrieve the information from the selected location's CarmenFeature

                CarmenFeature carmenFeature = PlacePicker.getPlace(data);
                


            }
        }
    }

    //geocoding redirect after seach
    protected void geoCoder(String data){
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(Mapbox.getAccessToken())
                .query(data)
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {

                    // Log the first results Point.
                    Point firstResultPoint = results.get(0).center();
                    Log.d(TAG, "onResponse: " + firstResultPoint.toString());
                    //get point data in geojson point format
                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());
                    //drop marker
                    GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                    if (source != null) {
                        source.setGeoJson(Feature.fromGeometry(firstResultPoint));
                    }
                    //get route
                    getRoute(originPoint, firstResultPoint);
                    button.setEnabled(true);



                } else {

                    // No result for your request were found.
                    Log.d(TAG, "onResponse: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

    }


    //taxi hub data to display

    //load points from geojson files in assets folder

    private String loadJsonFromAsset(String nameOfLocalFile) {
        try {
            InputStream is = getAssets().open(nameOfLocalFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception exception) {
            Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }

    //defining layer to display taxi stand loctions


    private void addTaxiIconSymbolLayer(@NonNull Style MapStyle) {
        Bitmap taxi_icon = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_stand);
        MapStyle.addImage("taxi-icon-id", taxi_icon);
        GeoJsonSource mysource = new GeoJsonSource("taxi-source-id", loadJsonFromAsset("taxi_stands.geojson"));
        MapStyle.addSource(mysource);
        SymbolLayer taxiSymbolLayer = new SymbolLayer("taxi-symbol-layer-id", "taxi-source-id");
        taxiSymbolLayer.withProperties(
                iconImage("taxi-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        MapStyle.addLayer(taxiSymbolLayer);
    }

    //remove taxi symbol layer
    private void removeTaxiIconLayer(@NonNull Style MapStyle ){
        if(mapboxMap != null && mapboxMap.getStyle() !=null){
            mapboxMap.getStyle().removeLayer("taxi-symbol-layer-id");
            mapboxMap.getStyle().removeSource("taxi-source-id");
        }
    }

    //police statios location data to display


    //defining layer to display taxi stand loctions


    private void addPoliceIconSymbolLayer(@NonNull Style MapStyle) {
        Bitmap police_icon = BitmapFactory.decodeResource(getResources(), R.drawable.police_station);
        MapStyle.addImage("police-icon-id", police_icon);
        GeoJsonSource mysource = new GeoJsonSource("police-source-id", loadJsonFromAsset("police_stations.geojson"));
        MapStyle.addSource(mysource);
        SymbolLayer policeSymbolLayer = new SymbolLayer("police-symbol-layer-id", "police-source-id");
        policeSymbolLayer.withProperties(
                iconImage("police-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        MapStyle.addLayer(policeSymbolLayer);

    }

    //remove police icon layer
    private void removePoliceIconLayer(@NonNull Style MapStyle ){
        if(mapboxMap != null && mapboxMap.getStyle() !=null){
            mapboxMap.getStyle().removeLayer("police-symbol-layer-id");
            mapboxMap.getStyle().removeSource("police-source-id");
        }
    }

    private void addHospitalIconSymbolLayer(@NonNull Style MapStyle) {
        Bitmap hospital_icon = BitmapFactory.decodeResource(getResources(), R.drawable.hospital);
        MapStyle.addImage("hospital-icon-id", hospital_icon);
        GeoJsonSource mysource = new GeoJsonSource("hospital-source-id", loadJsonFromAsset("hospitals.geojson"));
        MapStyle.addSource(mysource);
        SymbolLayer hospitalSymbolLayer = new SymbolLayer("hospital-symbol-layer-id", "hospital-source-id");
        hospitalSymbolLayer.withProperties(
                iconImage("hospital-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        MapStyle.addLayer(hospitalSymbolLayer);
    }

    //remove hospital layer
    private void removeHospitalIconLayer(@NonNull Style MapStyle ){
        if(mapboxMap != null && mapboxMap.getStyle() !=null){
            mapboxMap.getStyle().removeLayer("hospital-symbol-layer-id");
            mapboxMap.getStyle().removeSource("hospital-source-id");
        }
    }



    //map display methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}