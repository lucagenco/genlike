package com.luca.genlike.Chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private String idFacebook;
    private String profile_image;
    public ChatObject(String message, Boolean currentUser, String idFacebook, String profile_image) {
        this.message = message;
        this.currentUser = currentUser;
        this.idFacebook = idFacebook;
        this.profile_image = profile_image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
