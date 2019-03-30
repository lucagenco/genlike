package com.luca.genlike.Controller;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "app_prefs";
    private final static int PRIVATE_MODE = 0;
    private final static String SEX = "sex";
    private final static String IS_LOGGED = "is_logged";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";
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

    public String getPosition(){
        return prefs.getString(LATITUDE, "") + "," + prefs.getString(LONGITUDE, "");
    }

    public boolean isLogged(){
        return prefs.getBoolean(IS_LOGGED, false);
    }

    public String getSex(){
        return prefs.getString(SEX, "");
    }
    public void logout(){
        editor.clear();
    }
}
