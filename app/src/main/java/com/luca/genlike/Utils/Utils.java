package com.luca.genlike.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Utils {
    public static void changeActivity(Context currentActivity, Class secondActivity){
        Intent intent = new Intent(currentActivity, secondActivity);
        currentActivity.startActivity(intent);
    }

    public static void debug(Context contexte, String message){
        Toast.makeText(contexte,message, Toast.LENGTH_LONG).show();
    }
}
