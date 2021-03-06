package com.luca.genlike.Controller;

public class Cards {
    private String userID;
    private String first_name;
    private String id_facebook;
    private String birthday;
    private String profile_image;
    private String description;
    private String latitude;
    private String longitude;

    public Cards(String userID, String first_name, String id_facebook, String birthday, String profile_image, String description, String latitude, String longitude) {
        this.userID = userID;
        this.first_name = first_name;
        this.id_facebook = id_facebook;
        this.birthday = birthday;
        this.profile_image = profile_image;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId_facebook() {
        return id_facebook;
    }

    public void setId_facebook(String id_facebook) {
        this.id_facebook = id_facebook;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
