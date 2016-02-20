package com.test.test.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.test.test.PinpApp;
import com.test.test.R;
import com.test.test.models.AnywallPost;

/**
 * Created by Canpolat on 24/01/2016.
 */
public class PostActivity extends Activity {
    // UI references.
    private EditText postEditText;

    private Button postButton;

    private int maxCharacterCount = PinpApp.getConfigHelper().getPostMaxCharacterCount();
    private ParseGeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.option_choose);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);


        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();


        geoPoint = new ParseGeoPoint(latitude, longitude);

        postEditText = (EditText) findViewById(R.id.post_edittext);
        postEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePostButtonState();

            }
        });



        postButton = (Button) findViewById(R.id.button);
        postButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                post();
            }
        });

        updatePostButtonState();

    }}

    private void post () {
        String text = postEditText.getText().toString().trim();

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        // Create a post.
        AnywallPost post = new AnywallPost();

        // Set the location to the current user's location
        post.setLocation(geoPoint);
        post.setText(text);
        post.setUser(ParseUser.getCurrentUser());
        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        post.setACL(acl);

        // Save the post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private String getPostEditTextText () {
        return postEditText.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getPostEditTextText().length();
        boolean enabled = length > 0 && length < maxCharacterCount;
        postButton.setEnabled(enabled);
    }


}