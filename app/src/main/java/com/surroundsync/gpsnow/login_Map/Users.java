package com.surroundsync.gpsnow.login_Map;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class Users {
    String name;
    String userId;

    Users(){
        super();
    }

    public Users(String name, String userId) {
        super();
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
