package com.test.test;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class MarkerView extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    int locationCount = 0;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    public ImageView image1;
    View view;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_info_contents);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available


            // Getting reference to the SupportMapFragment of activitymain.xml
            MapFragment mapFrag =
                    (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            if (savedInstanceState == null) {
                mapFrag.getMapAsync(this);
            }

            try {

                // Getting GoogleMap object from the fragment
                if (googleMap == null) {
                    googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                }

                // Enabling MyLocation Layer of Google Map
                googleMap.setMyLocationEnabled(true);

                // Opening the sharedPreferences object
                sharedPreferences = getSharedPreferences("location", 0);

                // Getting number of locations already stored
                locationCount = sharedPreferences.getInt("locationCount", 0);

                // Getting stored zoom level if exists else return 0
                String zoom = sharedPreferences.getString("zoom", "0");

                // If locations are already saved
                if (locationCount != 0) {

                    String lat = "";
                    String lng = "";

                    // Iterating through all the locations stored
                    for (int i = 0; i < locationCount; i++) {

                        // Getting the latitude of the i-th location
                        lat = sharedPreferences.getString("lat" + i, "0");

                        // Getting the longitude of the i-th location
                        lng = sharedPreferences.getString("lng" + i, "0");

                        // Drawing marker on the map
                        drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                    }

                    // Moving CameraPosition to last clicked position
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

                    // Setting the zoom level in the map on last position  is clicked
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(16)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            googleMap.setOnMapClickListener(new OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    locationCount++;

                    // Drawing marker on the map
                    drawMarker(point);

                    /** Opening the editor object to write data to sharedPreferences */
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Storing the latitude for the i-th location
                    editor.putString("lat" + Integer.toString((locationCount - 1)), Double.toString(point.latitude));

                    // Storing the longitude for the i-th location
                    editor.putString("lng" + Integer.toString((locationCount - 1)), Double.toString(point.longitude));

                    // Storing the count of locations or marker count
                    editor.putInt("locationCount", locationCount);

                    /** Storing the zoom level to the shared preferences */
                    editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));

                    /** Saving the values stored in the shared preferences */
                    editor.commit();

                    Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();

                }
            });

            googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {

                    // Removing the marker and circle from the Google Map
                    googleMap.clear();

                    // Opening the editor object to delete data from sharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Clearing the editor
                    editor.clear();

                    // Committing the changes
                    editor.commit();

                    // Setting locationCount to zero
                    locationCount = 0;

                }
            });
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.

    }


    public void drawMarker(LatLng point) {


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        ImageView image1 = (ImageView) findViewById(R.id.main_image);



        LinearLayout tv = (LinearLayout) this.getLayoutInflater().inflate(R.layout.main_image, null, true);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.setDrawingCacheEnabled(true);
        tv.buildDrawingCache();
        Bitmap bmp = tv.getDrawingCache();


        /*Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(800, 800, conf);
        Canvas canvas1 = new Canvas(bmp);*/

        // paint defines the text color,
        // stroke width, size
        // Paint color = new Paint();
        //color.setTextSize(35);
        //color.setColor(Color.BLACK);


        //modify canvas
        //canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
        //  R.drawable.appetizer), 0, 0, color);
        //canvas1.drawText("User Name!", 30, 40, color);


        // set Icon is Bmp thats how it pulls the data.
        googleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .alpha(0.4f)
                .position(point))
                .setIcon(BitmapDescriptorFactory.fromFile("PinpApp/my images/image_5"));


    }

    //launching the camera

    public void launchCamera(View view) {
        int imageNum = 0;
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "PinpApp/my images");
        imagesFolder.mkdirs(); // <----
        String fileName = "image_" + String.valueOf(imageNum) + ".png";
        File output = new File(imagesFolder, fileName);

        while (output.exists()) {
            imageNum++;
            fileName = "image_" + String.valueOf(imageNum) + ".png";
            output = new File(imagesFolder, fileName);
        }

        Uri uriSavedImage = Uri.fromFile(output);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        // take image and pass results along to onActivityResult
        //startActivity(imageIntent);



        // take image and pass results along to onActivityResult

        //startActivity(imageIntent);
        startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //get photo
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromFile("image_5")));

            imageView.setImageBitmap(photo);



        }


        }





}

