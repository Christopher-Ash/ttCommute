package com.example.ttcommute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

// Map initialization classes
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
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

import static com.google.gson.internal.bind.TypeAdapters.URI;
import static com.mapbox.api.directions.v5.DirectionsCriteria.ANNOTATION_SPEED;
import static com.mapbox.api.directions.v5.DirectionsCriteria.DESTINATION_ANY;
import static com.mapbox.api.directions.v5.DirectionsCriteria.GEOMETRY_POLYLINE;
import static com.mapbox.api.directions.v5.DirectionsCriteria.OVERVIEW_FULL;
import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_DRIVING;
import static com.mapbox.api.geocoding.v5.MapboxGeocoding.*;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// Route calculation classes
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
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
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapLongClickListener,
        PermissionsListener,OnRouteSelectionChangeListener, MapboxMap.OnMapClickListener{
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    //navigation view
    private NavigationView navigationView;
    private MapboxNavigation mapboxNavigation;
    // variables for calculating and drawing a route
    private List <DirectionsRoute> currentRoute =  new ArrayList<>();
    private DirectionsRoute chosenRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private FloatingActionButton button;
    //variables for search
    private FloatingActionButton search;
    private static final int REQUEST_CODE_AUTOCOMPLETE =1;
    public int backgrounColor() {
        return 0;
    }
    //place picker variable
    private static final int PLACE_SELECTION_REQUEST_CODE = 56789;
    //taxi stand locator variables
    private FeatureCollection featureCollection;
    private FeatureCollection featureCollection2;
    private FeatureCollection featureCollection3;
    private FeatureCollection featureCollection4;
    private static final String PROPERTY_SELECTED = "selected";
    //filter display variables
    private CheckBox showTaxi;            //chip to show taxis
    private CheckBox showServices;        //chip to show essential serices
    //injected features variables
    private CarmenFeature lh_taxistand;
    private CarmenFeature cf_taxistand;
    //waypoint variables
    private List<Point> pt_points = new ArrayList<>();
    //TextView to show route info
    private TextView info;
    private int cost;
    private double distance;
    private double duration;
    private Button pt;
    private Button pvt;





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
        //adding waypoints
     /*   pt_points.add(Point.fromLngLat(-61.22651474111301, 10.627847164740515));
        pt_points.add(Point.fromLngLat( -61.226810293009336, 10.629474955668414));
        pt_points.add(Point.fromLngLat( -61.24142911690379, 10.629349883107878));
        pt_points.add(Point.fromLngLat(  -61.2541131781645, 10.620464117381403));
        pt_points.add(Point.fromLngLat( -61.26560529749544, 10.64244596927449));
        pt_points.add(Point.fromLngLat(-61.27379146469815, 10.641031398022207));
        pt_points.add(Point.fromLngLat(-61.27918893757342, 10.641473452249429));
        pt_points.add(Point.fromLngLat( -61.281657, 10.641611));
        pt_points.add(Point.fromLngLat(-61.282518, 10.641558));
        pt_points.add(Point.fromLngLat(-61.28452294350522, 10.641196972327819));
        pt_points.add(Point.fromLngLat(  -61.283916535985526, 10.638724752994207));
        pt_points.add(Point.fromLngLat( -61.292338862708775, 10.634332099573243));
        pt_points.add(Point.fromLngLat(   -61.29462973553771, 10.6285266364807));
        pt_points.add(Point.fromLngLat(  -61.30554507092826, 10.626252983950707));
        pt_points.add(Point.fromLngLat(-61.320727718501686, 10.627820260727674));
        pt_points.add(Point.fromLngLat( -61.33081205100312, 10.635126755007192));
        pt_points.add(Point.fromLngLat(  -61.33512428227614, 10.63559030333208));*/


        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        try {
            getFeatureCollectionFromJson();
        } catch (Exception exception) {
            Log.e("MapActivity", "onCreate: " + exception);
            Toast.makeText(this, R.string.failure_to_load_file, Toast.LENGTH_LONG).show();
        }


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

                //publica nad private transport button setup
                pt = (Button)findViewById(R.id.button);
                pt.setVisibility(View.INVISIBLE);

                pvt = (Button)findViewById(R.id.button2);
                pvt.setVisibility(View.INVISIBLE);

                //Text view setup
                info = (TextView)findViewById(R.id.textView);
                //disable initially
                info.setVisibility(View.INVISIBLE);
                addUserLocations();


                mapboxMap.addOnMapLongClickListener(MainActivity.this);
                mapboxMap.addOnMapClickListener(MainActivity.this);

                showTaxi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox) view).isChecked();
                        if (checked)
                            addIconSymbolLayer(R.drawable.taxi_stand, "taxi-icon-id",
                                    "taxi-source-id", "taxi_stands.geojson", "taxi-symbol-layer-id");
                        else

                            //remove layer
                            removeIconLayer("taxi-symbol-layer-id", "taxi-source-id");
                    }
                });

                showServices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox) view).isChecked();
                        if (checked) {
                            //add hospital and police station symbol layers
                            addIconSymbolLayer(R.drawable.hospital, "hospital-icon-id",
                                    "hospital-source-id", "hospitals.geojson", "hospital-symbol-layer-id");
                            addIconSymbolLayer(R.drawable.police_station, "police-icon-id",
                                    "police-source-id", "police_stations.geojson", "police-symbol-layer-id");


                        } else {
                            //remove layers
                            removeIconLayer("hospital-symbol-layer-id", "hospital-source-id");
                            removeIconLayer("police-symbol-layer-id", "police-source-id");
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
                        if (chosenRoute == null) {
                            Toast.makeText(MainActivity.this, "Route not found. Navigation halted", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean simulateRoute = false;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(chosenRoute)
                                    .shouldSimulateRoute(simulateRoute)
                                    .build();
                            // Call this method with Context from within an Activity
                            NavigationLauncher.startNavigation(MainActivity.this, options);



                        }
                    }
                });
            }
        });

        List<Feature> featureList = featureCollection.features();
        List<Feature> featureList2 = featureCollection2.features();
        List<Feature> featureList3 = featureCollection3.features();


        if (featureList != null) {

            for (int x = 0; x < featureList.size(); x++) {

                Feature singleLocation = featureList.get(x);

                // Get the single location's String properties to place in its map marker
                String singleLocationName = singleLocation.getStringProperty("name");
            }
        }

        if (featureList2 != null) {

            for (int x = 0; x < featureList2.size(); x++) {

                Feature singleLocation = featureList2.get(x);

                // Get the single location's String properties to place in its map marker
                String singleLocationName = singleLocation.getStringProperty("name");
            }
        }

        if (featureList3 != null) {

            for (int x = 0; x < featureList3.size(); x++) {

                Feature singleLocation = featureList3.get(x);

                // Get the single location's String properties to place in its map marker
                String singleLocationName = singleLocation.getStringProperty("name");
            }
        }


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

    //showing info
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

// Convert LatLng coordinates to screen pixel and only query the rendered features.
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

        // Get the first feature within the list if one exist
        if (features.size() > 0) {
            Feature feature = features.get(0);

            // Ensure the feature has properties defined
            if (feature.properties() != null) {
                for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                    // Log all the properties
                    Log.d(TAG, String.format("%s = %s", entry.getKey(), entry.getValue()));
                }
            }
        }
        return false;
    }


    //method to drop marker and draw route when map is clicked
    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
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
        return true;
    }



    //method to calculate route between user location and marker
    private void getRoute(Point origin, Point destination) {


        NavigationRoute.builder(this)      //rename as builder to  put waypoints
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .alternatives(true)
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

                        currentRoute = response.body().routes();
                        distance = Math.round(response.body().routes().get(0).distance()/1000);
                        duration = Math.round(response.body().routes().get(0).duration()/60);

                        //make public and private buttons appear
                        pt.setVisibility(View.VISIBLE);
                        pvt.setVisibility(View.VISIBLE);

                        pt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getPublicRoute(origin, destination);
                                pt.setTextColor(getResources().getColor(R.color.mapboxWhite));
                                pt.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
                                pvt.setTextColor(getResources().getColor(R.color.BLACK));
                                pvt.setBackgroundColor(getResources().getColor(R.color.mapboxWhite));
                            }

                        });


                        if(info.getVisibility() == View.INVISIBLE){
                            info.setVisibility(View.VISIBLE);
                        }

                        //set text view to display info
                        info.setText("Distance: " + distance + "km\nDuration: " +duration+"min");


                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            //deprecated function
                            //navigationMapRoute.removeRoute();
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                            navigationMapRoute.updateRouteVisibilityTo(false);
                            navigationMapRoute.showAlternativeRoutes(true);

                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }

                        navigationMapRoute.addRoutes(currentRoute);
                        //let chosen route be default route
                        chosenRoute = currentRoute.get(0);
                        navigationMapRoute.setOnRouteSelectionChangeListener(MainActivity.this::onNewPrimaryRouteSelected);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });

    }
    //method for navigation on selected route
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        chosenRoute = directionsRoute;
        distance = Math.round(chosenRoute.distance()/1000);
        duration = Math.round(chosenRoute.duration()/60);
        info.setText("Distance: " + distance + "km\nDuration: " +duration+"min");
    }

    //public transport using map matching
    private void getPublicRoute(Point originPoint, Point destinationPoint){
        publicRouteInfo(originPoint, destinationPoint);

        NavigationRoute.Builder builder =NavigationRoute.builder(this)      //rename as builder to  put waypoints
                .accessToken(Mapbox.getAccessToken())
                .origin(originPoint)
                .destination(destinationPoint)
                .profile(PROFILE_DRIVING);
        for (Point waypoint : pt_points) {
            builder.addWaypoint(waypoint);
        }
        builder
                .addWaypointIndices(0,1, pt_points.size(), pt_points.size()+1)
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

                        chosenRoute = response.body().routes().get(0);
                        distance = Math.round(response.body().routes().get(0).distance()/1000);
                        duration = Math.round(response.body().routes().get(0).duration()/60);

                        pvt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getRoute(originPoint, destinationPoint);
                                pvt.setTextColor(getResources().getColor(R.color.mapboxWhite));
                                pvt.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
                                pt.setTextColor(getResources().getColor(R.color.BLACK));
                                pt.setBackgroundColor(getResources().getColor(R.color.mapboxWhite));
                            }

                        });

                        if(info.getVisibility() == View.INVISIBLE){
                            info.setVisibility(View.VISIBLE);
                        }

                        //set text view to display info
                        info.setText("\nDistance: " + distance + "km\nDuration: " +duration+ "min\nCost: $" +cost);



                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            //deprecated function
                            //navigationMapRoute.removeRoute();
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                            navigationMapRoute.updateRouteVisibilityTo(false);
                            navigationMapRoute.showAlternativeRoutes(true);

                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }

                        navigationMapRoute.addRoute(chosenRoute);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });



    }

    private void publicRouteInfo(Point originPoint, Point destinationPoint){

        List<Feature> features = featureCollection4.features();
        Feature feature = features.get(0);

        pt_points = ((LineString) Objects.requireNonNull(feature.geometry())).coordinates();

        cost = 10;

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
                        .addInjectedFeature(lh_taxistand)
                        .addInjectedFeature(cf_taxistand)
                        .backgroundColor(Color.parseColor("#1094A5"))
                        .build(PlaceOptions.MODE_FULLSCREEN)

                )
                .build(this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        //option to clear recent history
        //PlaceAutocomplete.clearRecentHistory(this);

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



    //turning geojson files into feature collection
    private void getFeatureCollectionFromJson() throws IOException {
        try {
            // Use fromJson() method to convert the GeoJSON file into a usable FeatureCollection object
            featureCollection = FeatureCollection.fromJson(loadJsonFromAsset("taxi_stands.geojson"));
            featureCollection2 = FeatureCollection.fromJson(loadJsonFromAsset("police_stations.geojson"));
            featureCollection3 = FeatureCollection.fromJson(loadJsonFromAsset("hospitals.geojson"));
            featureCollection4 = FeatureCollection.fromJson(loadJsonFromAsset("publicroutes.geojson"));


        } catch (Exception exception) {
            Log.e("MapActivity", "getFeatureCollectionFromJson: " + exception);
        }
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

    //add icon layer
    private void addIconSymbolLayer(int image, String icon_id, String source_id, String gj_file, String layer_id) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),image);
        mapboxMap.getStyle().addImage(icon_id, icon);
        GeoJsonSource mysource = new GeoJsonSource(source_id, loadJsonFromAsset(gj_file));
        mapboxMap.getStyle().addSource(mysource);
        SymbolLayer symbolLayer = new SymbolLayer(layer_id, source_id);
        symbolLayer.withProperties(
                iconImage(icon_id),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        mapboxMap.getStyle().addLayer(symbolLayer);
    }
    //remove icon layer

    private void removeIconLayer(String layerID, String sourceID ){
        if(mapboxMap != null && mapboxMap.getStyle() !=null){
            mapboxMap.getStyle().removeLayer(layerID);
            mapboxMap.getStyle().removeSource(sourceID);
        }
    }

    private void addUserLocations() {
        lh_taxistand = CarmenFeature.builder().text("La Horquetta Taxi Stand")
                .geometry(Point.fromLngLat(-61.281468, 10.64088))
                .placeName("LaHorquetta Taxi Stand")
                .id("directions-lh")
                .properties(new JsonObject())
                .build();

        cf_taxistand = CarmenFeature.builder().text("Cane Farm TAxi Stand")
                .placeName("Cane Farm Taxi Stand")
                .geometry(Point.fromLngLat( -61.352935, 10.640352))
                .id("directions-cf")
                .properties(new JsonObject())
                .build();
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
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
            mapboxMap.removeOnMapLongClickListener(this);

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}