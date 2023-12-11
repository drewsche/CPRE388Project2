package com.example.gcache.model;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * This class represents the parameters that are
 * included in a user's post
 */
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

    /**
     * Display's information for parameters of user's post
     * @param dateTime date and time post was created
     * @param distance distance between user and home location
     * @param locationCoords current coordinates
     * @param locationName string name of location
     * @param metPerson string name of person met for post
     * @param photo image of post made
     * @param points points accrued by post
     * @param user user's name
     * @param visibility sets if post is public or private
     */
    public Post(double distance,
                GeoPoint locationCoords,
                String locationName,
                String metPerson,
                String photo,
                int points,
                String poster,
                String posterUID,
                String visibility) {
        this.dateTime = Timestamp.now();
        this.distance = distance;
        this.locationCoords = locationCoords;
        this.locationName = locationName;
        this.metPerson = metPerson;
        this.photo = photo;
        this.points = points;
        this.poster = poster;
        this.posterUID = posterUID;
        this.visibility = visibility;
    }

    /**
     * Gets date and time of post
     * @return val date and time
     */
    public Timestamp getDateTime() {
        return dateTime;
    }
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets distance of post
     * @return val of distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets distance of post
     * @param distance val of distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Get coordinates of post
     * @return val of location coordinates
     */
    public GeoPoint getLocationCoords() {
        return locationCoords;
    }

    /**
     * Sets location coordinates
     * @param locationCoords val of location coordinates
     */
    public void setLocationCoords(GeoPoint locationCoords) {
        this.locationCoords = locationCoords;
    }

    /**
     * Gets name of location from post
     * @return name of location
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Sets name of location
     * @param locationName string of location name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Name of person met
     * @return string of name person
     */
    public String getMetPerson() {
        return metPerson;
    }

    /**
     * Sets person met
     * @param metPerson string name of person met
     */
    public void setMetPerson(String metPerson) {
        this.metPerson = metPerson;
    }

    /**
     * Gets photo of post
     * @return image of photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets image of photo
     * @param photo image of photo of post
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Gets points accrued by post
     * @return num value of points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets points of post
     * @param points num value of total points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Gets poster id
     * @return id of poster
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Sets poster id
     * @param poster id of psoter
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Gets UID of poster
     * @return UID val of poster
     */
    public String getPosterUID() {
        return posterUID;
    }

    /**
     * Sets UID of psoter
     * @param posterUID val of UID of poster
     */
    public void setPosterUID(String posterUID) {
        this.posterUID = posterUID;
    }

    /**
     * Gets visibility of post
     * @return status of visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets visibility of post
     * @param visibility status of visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}