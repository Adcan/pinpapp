package com.test.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.test.view.CameraPrint;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap googleMap;

    private LocationRequest locationRequest;

    private Location lastLocation;
    private Location currentLocation;


    /*
 * Constants for location update parameters
 */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;


    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;


    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;




    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;


    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_picture_fragment);

        MapFragment mapFrag =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

        try {

            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);

            googleMap.setTrafficEnabled(false);
            // Create a new global location parameters object
            locationRequest = LocationRequest.create();

            // Use high accuracy
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Set the update interval
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

            // Set the interval ceiling to one minute
            locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

            googleMap.setIndoorEnabled(true);

            googleMap.setBuildingsEnabled(true);

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getMyLocation().getLatitude();

            googleMap.getMyLocation().getLongitude();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // intent will be used to start anb activity. points to the java class 'pinsfeedActivity' inthis class there are  instructions to return a layout view.
    // note that we must use MenuItem for items in the action bar.

    public void onMapReady(final GoogleMap googleMap) {
        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.my_pins, null);
                LatLng latlng = arg0.getPosition();
                LatLng ll = arg0.getPosition();
                return v;
            }
        });

        // Adding and showing marker while touching the GoogleMap
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // Clears any existing markers from the GoogleMap
                googleMap.clear();

                // Creating an instance of MarkerOptions to set position
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting position on the MarkerOptions
                markerOptions.position(arg0);

                // Animating to the currently touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));

                // Adding marker on the GoogleMap
                Marker marker = googleMap.addMarker(markerOptions);

                // Showing InfoWindow on the GoogleMap
                marker.showInfoWindow();

            }
        });

        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(89)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .alpha(0.4f)
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(new LatLng(location.getLatitude(), location.getLongitude())));

            // Set up the handler for the post button click


        } else {
            Toast.makeText(MainActivity.this,
                    "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_view_photos)
    public void onViewPhotosButtonClicked(View view) {
        startActivity(new Intent(this, PhotoActivity.class));
    }

    //launching the camera
    public void launchCamera(View view) {

        Intent camera = new Intent(this, CameraPrint.class);
        startActivity(camera);


    }


}
