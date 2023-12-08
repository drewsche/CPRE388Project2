package com.example.gcache.model;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {

    public static final String FIELD_DATE_TIME = "dateTime";
//    public static final String FIELD_LOCATION_COORDS = "locationCoords";
//    public static final String FIELD_LOCATION_NAME = "locationName";
//    public static final String FIELD_MET_PERSON = "metPerson";
//    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_POINTS = "points";
//    public static final String FIELD_POSTER = "poster";
//    public static final String FIELD_POSTER_UID = "posterUID";
//    public static final String FIELD_VISIBILITY = "visibility";

    private Timestamp dateTime;
    private double distance;
    private GeoPoint locationCoords;
    private String locationName;
    private String metPerson;
    private String photo;
    private int points;
    private String poster;
    private String posterUID;
    private String visibility;

    public Post() {}

    public Post(Timestamp dateTime,
                double distance,
                GeoPoint locationCoords,
                String locationName,
                String metPerson,
                String photo,
                int points,
                FirebaseUser user,
                String visibility) {
        this.dateTime = dateTime;
        this.distance = distance;
        this.locationCoords = locationCoords;
        this.locationName = locationName;
        this.metPerson = metPerson;
        this.photo = photo;
        this.points = points;
        this.poster = user.getDisplayName();
        this.posterUID = user.getUid();
        this.visibility = visibility;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GeoPoint getLocationCoords() {
        return locationCoords;
    }
    public void setLocationCoords(GeoPoint locationCoords) {
        this.locationCoords = locationCoords;
    }

    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getMetPerson() {
        return metPerson;
    }
    public void setMetPerson(String metPerson) {
        this.metPerson = metPerson;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosterUID() {
        return posterUID;
    }
    public void setPosterUID(String posterUID) {
        this.posterUID = posterUID;
    }

    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}