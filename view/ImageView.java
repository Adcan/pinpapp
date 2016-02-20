package com.test.test.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.test.test.R;
import com.test.test.models.Location;
import com.test.test.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canpolat on 07/02/2016.
 */
public class ImageView extends Activity {

    android.widget.ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pins);


        Location.getQuery().whereEqualTo("photographer", ParseUser.getCurrentUser()).include("photo").findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> list, ParseException e) {
                if (e == null) {
                    photoImageView = (android.widget.ImageView) findViewById(R.id.imageView);


                } else{
                    e.printStackTrace();
                }
            }
        });



    }



}
