package com.surroundsync.gpsnow;

/**
 * Created by Ashray Joshi on 15-Jun-16.
 */
public class UserDetails {

    String name;
    String latitude;
    String longitude;
    boolean status;
    String userId;


    UserDetails(){

    }

    public UserDetails(String name, String latitude, String longitude, boolean status, String userId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
