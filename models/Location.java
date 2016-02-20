package com.test.test.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Canpolat on 24/01/2016.
 */
@ParseClassName("Location")
public class Location extends ParseObject {

    public static ParseQuery<Location> getQuery() {
        return new ParseQuery<Location>("Location");
    }

    public ParseUser getPhotographer() {
        return getParseUser("photographer");
    }

    public void setPhotographer(ParseUser photographer) {
        put("photographer", photographer);
    }

    public ParseGeoPoint setLocation(ParseGeoPoint geopoint1) {

        put("geopoint", geopoint1);
        return getParseGeoPoint("geopoint");
    }

    public ParseUser getTarget() {
        return getParseUser("target");
    }

    public void setTarget(ParseUser target) {
        put("target", target);
    }

    public Photo getPhoto() {
        return (Photo) get("photo");
    }

    public void setPhoto(Photo photo) {
        put("photo", photo);
    }
}
