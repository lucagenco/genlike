package com.luca.genlike.Controller;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "app_prefs";
    private final static int PRIVATE_MODE = 0;
    private final static String SEX = "sex";
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

    public String getSex(){
        return prefs.getString(SEX, "");
    }
    public void logout(){
        editor.clear();
    }
}
