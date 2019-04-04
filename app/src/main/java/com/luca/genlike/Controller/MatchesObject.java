package com.luca.genlike.Controller;

public class MatchesObject {
    private String userID;
    private String first_name;
    private String id_facebook;
    private String profile_image;

    public MatchesObject(String userID, String first_name, String id_facebook, String profile_image) {
        this.userID = userID;
        this.first_name = first_name;
        this.id_facebook = id_facebook;
        this.profile_image = profile_image;
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

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
