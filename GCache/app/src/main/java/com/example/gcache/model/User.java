package com.example.gcache.model;

import com.google.firebase.firestore.GeoPoint;

/**
 * Class representing a user.
 */
public class User {

    private String userId;
    private String displayName;
    private GeoPoint home;
    private int points;
    private String profilePic;

    public User() {}

    public User(String displayName,
                GeoPoint home,
                int points,
                String profilePic) {
        this.displayName = displayName;
        this.home = home;
        this.points = points;
        this.profilePic = profilePic;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GeoPoint getHome() {
        return home;
    }
    public void setHome(GeoPoint home) {
        this.home = home;
    }

    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public String getProfilePic() {
        return profilePic;
    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
