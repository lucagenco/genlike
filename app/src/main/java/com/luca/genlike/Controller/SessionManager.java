package com.luca.genlike.Controller;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "app_prefs";
    private final static int PRIVATE_MODE = 0;
    private final static String SEX = "sex";
    private final static String ID_FACEBOOK = "id_facebook";
    private final static String AGE = "age";
    private final static String DAY_BIRTHDAY = "day_birthday";
    private final static String MONTH_BIRTHDAY = "month_birthday";
    private final static String YEAR_BIRTHDAY = "year_birthday";
    private final static String DESCRIPTION = "description";
    private final static String PROFILE_IMAGE = "profile_image";
    private final static String IS_LOGGED = "is_logged";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";
    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private Context context;


    public SessionManager(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void setSex(String sex){
        editor.putString(SEX, sex);
        editor.commit();
    }

    public void setIsLogged(boolean isLoggeded){
        editor.putBoolean(IS_LOGGED, isLoggeded);
        editor.commit();
    }

    public void setPosition(String latitude, String longitude){
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }

    public void setIdFacebook(String idFacebook){
        editor.putString(ID_FACEBOOK, idFacebook);
        editor.commit();
    }

    public void setAge(int age){
        editor.putInt(AGE, age);
        editor.commit();
    }

    public void setDescription(String description){
        editor.putString(DESCRIPTION, description);
        editor.commit();
    }

    public void setProfileImage(String profileImage){
        editor.putString(PROFILE_IMAGE, profileImage);
        editor.commit();
    }

    public void setFirstName(String firstName){
        editor.putString(FIRST_NAME, firstName);
        editor.commit();
    }

    public void setLastName(String lastName){
        editor.putString(LAST_NAME, lastName);
        editor.commit();
    }

    public void setDayBirthday(int day){
        editor.putInt(DAY_BIRTHDAY, day);
        editor.commit();
    }

    public void setMonthBirthday(int monthBirthday){
        editor.putInt(MONTH_BIRTHDAY, monthBirthday);
        editor.commit();
    }

    public void setYearBirthday(int yearBirthday){
        editor.putInt(YEAR_BIRTHDAY, yearBirthday);
        editor.commit();
    }

    public String getBirthday(){
        return this.getYearBirthday() + "/" + this.getMonthBirthday() + "/" + this.getDayBirthday();
    }

    public String getPosition(){
        return prefs.getString(LATITUDE, "") + "," + prefs.getString(LONGITUDE, "");
    }

    public String getLattitude(){
        return prefs.getString(LATITUDE, "");
    }

    public String getLongitude(){
        return prefs.getString(LONGITUDE, "");
    }

    public boolean isLogged(){
        return prefs.getBoolean(IS_LOGGED, false);
    }

    public String getSex(){
        return prefs.getString(SEX, "");
    }

    public String getIdFacebook(){
        return prefs.getString(ID_FACEBOOK, "");
    }

    public int getAge(){
        return prefs.getInt(AGE, 0);
    }

    public String getDescription(){
        return prefs.getString(DESCRIPTION, "");
    }

    public String getProfileImage(){
        return prefs.getString(PROFILE_IMAGE, "");
    }

    public String getFirstName(){
        return prefs.getString(FIRST_NAME, "");
    }

    public String getLastName(){
        return prefs.getString(LAST_NAME, "");
    }

    public int getDayBirthday(){
        return prefs.getInt(DAY_BIRTHDAY, 0);
    }

    public int getMonthBirthday(){
        return prefs.getInt(MONTH_BIRTHDAY, 0);
    }

    public int getYearBirthday(){
        return prefs.getInt(YEAR_BIRTHDAY, 0);
    }

    public void deleteAll(){
        editor.clear().commit();
    }
}
