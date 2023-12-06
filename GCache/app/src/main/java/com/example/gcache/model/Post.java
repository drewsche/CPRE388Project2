package com.example.gcache.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {

    public static final String FIELD_DATE_TIME = "dateTime";
    public static final String FIELD_IS_PUBLIC = "isPublic";
    public static final String FIELD_LOCATION_COORDS = "locationCoords";
    public static final String FIELD_LOCATION_NAME = "locationName";
    public static final String FIELD_MET_PERSON = "metPerson";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_POINTS = "points";
    public static final String FIELD_POSTER = "poster";

    private Timestamp dateTime;
    private boolean isPublic;
    private GeoPoint locationCoords;
    private String locationName;
    private String metPerson;
    private String photo;
    private int points;
    private String poster;

    public Post() {}

    public Post(Timestamp dateTime,
                boolean isPublic,
                GeoPoint locationCoords,
                String locationName,
                String metPerson,
                String photo,
                int points,
                String poster) {
        this.dateTime = dateTime;
        this.isPublic = isPublic;
        this.locationCoords = locationCoords;
        this.locationName = locationName;
        this.metPerson = metPerson;
        this.photo = photo;
        this.points = points;
        this.poster = poster;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public boolean getIsPublic() {
        return isPublic;
    }
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
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
}