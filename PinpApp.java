package com.test.test;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.test.test.models.Location;
import com.test.test.models.Photo;
import com.test.test.utils.ConfigHelper;

/**
 * Created by a.canpolat on 16/12/2015.
 */
public class PinpApp extends Application {

    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "AnyWall";

    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;

    public PinpApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // [Optional] Power your app with Local Datastore. For more info, go to
// https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "kv1JnxIbv00vjKWX7WRswHvbJGF55dWOBNPrpHRz", "2E9Vo4EmHhUA0dIj71tDqQVYbxeDXIcNG6rN2nvw");
        ParseFacebookUtils.initialize(this);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Location.class);
        ParseInstallation.getCurrentInstallation();

        preferences = getSharedPreferences("com.parse.anywall", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();

    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

}
