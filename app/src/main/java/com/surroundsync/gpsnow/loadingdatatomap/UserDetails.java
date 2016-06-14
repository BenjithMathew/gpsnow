package com.surroundsync.gpsnow.loadingdatatomap;

/**
 * Created by Devil on 14-06-2016.
 */
public class UserDetails {

    private String name;
    private String latitude;
    private String longitude;
    private boolean status;

    UserDetails(){

    }

    public UserDetails(String name, String latitude, String longitude, boolean status) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
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
}
