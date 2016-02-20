package com.test.test.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Canpolat on 27/12/2015.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject {
    public ParseUser getPhotographer() {
        return getParseUser("photographer");
    }

    public void setPhotographer(ParseUser photographer) {
        put("photographer", photographer);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

}
