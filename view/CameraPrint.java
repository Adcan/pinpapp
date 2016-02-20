package com.test.test.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.test.test.PhotoActivity;
import com.test.test.R;
import com.test.test.models.Photo;
import com.test.test.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.parse.ParseUser.*;

public class CameraPrint extends AppCompatActivity {
    private int GALLERY_REQUEST_CODE = 2312;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_choose);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_take_photo)
    public void onTakePhotoButtonClicked() {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(imageIntent, GALLERY_REQUEST_CODE);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_view_photos)
    public void onViewPhotosButtonClicked() {
        startActivity(new Intent(this, PhotoActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                fetchFriends(new FriendsReadyListener() {
                    @Override
                    public void onFriendsReady(List<ParseUser> friends) {
                        if (friends != null)
                            savePhoto(data.getData(), friends);
                    }
                });
            }
        }
    }

    private void fetchFriends(final FriendsReadyListener listener) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject responseJson = response.getJSONObject();
                        String[] ids = extractIdsForJSON(responseJson);
                        fetchFriendForId(ids, listener);
                    }
                }
        ).executeAsync();
    }

    private String[] extractIdsForJSON(JSONObject responseJson) {
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            String[] ids = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                ids[i] = data.optJSONObject(i).optString("id");
            }

            return ids;
        }

        return null;
    }

    private void fetchFriendForId(String[] ids, final FriendsReadyListener listener) {
        List<ParseQuery<ParseUser>> friendsQueries = new ArrayList<>();
        for (String id : ids) {
            friendsQueries.add(getQuery()
                    .whereEqualTo("facebookId", id));
        }

        ParseQuery.or(friendsQueries)
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        listener.onFriendsReady(list);
                    }
                });
    }

    private void savePhoto(Uri pathToImage, final List<ParseUser> targets) {
        byte[] pictureContents = FileUtils.loadImage(pathToImage, this);
        if (pictureContents != null) {
            final Photo photo = new Photo();

            photo.setPhoto(new ParseFile("image.png",pictureContents));
            photo.setPhotographer(getCurrentUser());

            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(CameraPrint.this, "Successfully saved photo", Toast.LENGTH_SHORT).show();
                        createLocation(photo);
                        createPhotoTargets(photo, targets);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private void createLocation(Photo photo){


        final com.test.test.models.Location geopoint1 = new com.test.test.models.Location();


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        ParseGeoPoint geopoint2 = new ParseGeoPoint(latitude, longitude);
        geopoint1.setLocation(geopoint2);
        geopoint1.setPhotographer(getCurrentUser());
        geopoint1.setPhoto(photo);
        geopoint1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(CameraPrint.this, "Successfully saved geolocation", Toast.LENGTH_SHORT).show();

                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    private void createPhotoTargets(Photo photo, final List<ParseUser> targets) {
        List<com.test.test.models.Location> targetsToSave = new ArrayList<>();
        for (ParseUser userTarget : targets) {
            com.test.test.models.Location photographer = new com.test.test.models.Location();
            photographer.setPhoto(photo);
            photographer.setTarget(userTarget);
            targetsToSave.add(photographer);
        }

        ParseObject.saveAllInBackground(targets, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    onTargetsSaved(targets);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onTargetsSaved(List<ParseUser> users) {
        List<ParseQuery<ParseInstallation>> pushQueries = new ArrayList<>();
        for (ParseUser user : users) {
            ParseQuery<ParseInstallation> pushNotifQuery = ParseInstallation.getQuery()
                    .whereEqualTo("user", user);
            pushQueries.add(pushNotifQuery);
        }

        pushQueries.add(ParseInstallation.getQuery()
                .whereEqualTo("user", getCurrentUser()));
        ParsePush.sendMessageInBackground("Hey there, you have a new message", ParseQuery.or(pushQueries),
                new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(CameraPrint.this, "Sent notifications to friends!", Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private interface FriendsReadyListener {
        void onFriendsReady(List<ParseUser> friends);
    }
}
