package com.test.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.test.test.models.Photo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by Canpolat on 18/01/2016.
 */
public class UserList extends AppCompatActivity {

    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter1;
    ListView listView;
    Bitmap bitmap;

    public void checkForImages(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("recipientUsername", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> images, ParseException e) {
                if (e == null) {

                    if (images.size()> 0){
                        for (ParseObject image : images) {


                            ParseFile file = image.getParseFile("image");
                            byte[] byteArray = new byte[0];
                            try {
                                byteArray = file.getData();
                                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            if (bitmap != null) {


                                AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);

                                builder.setTitle("You have a new message!");
                                TextView content = new TextView(UserList.this);

                                content.setGravity(Gravity.CENTER_HORIZONTAL);

                                content.setText(image.getString("senderUsername") + "has sent you an image");
                                builder.setView(content);

                                builder.setPositiveButton("View", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("AppInfo", "Display Image");

                                        AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);

                                        builder.setTitle("Your message");
                                        ImageView content = new ImageView(UserList.this);
                                        content.setImageBitmap(bitmap);
                                        builder.setView(content);

                                        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Log.i("AppInfo", "Image Closed");
                                            }
                                        });

                                        builder.show();
                                    }

                                });

                                builder.show();

                            }
                        }

                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject image = new ParseObject("Image");
                //ParseObject photographer = new ParseObject("Photographer");

                image.put("senderUsername", ParseUser.getCurrentUser().getObjectId());
                image.put("recipientUsername", usernames.get(requestCode));
                image.put("image", file);



                image.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            Toast.makeText(getApplicationContext(), "Image sent successfully!", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(getApplicationContext(), "Image could not be sent - please try again", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        usernames = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
        listView.setAdapter(arrayAdapter1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, position);


            }


        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        usernames.clear();

                        //look for parse username in the 'user' section
                        for (ParseUser user : objects){
                            usernames.add(user.getUsername());
                        }

                        arrayAdapter1.notifyDataSetChanged();
                    }
                } else {
                    // Something went wrong.
                }
            }
        });

        checkForImages();
    }


}
