package com.luca.genlike.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class Utils {
    public static void changeActivity(Context currentActivity, Class secondActivity){
        Intent intent = new Intent(currentActivity, secondActivity);
        currentActivity.startActivity(intent);
    }

    public static void debug(Context contexte, String message){
        Toast.makeText(contexte,message, Toast.LENGTH_LONG).show();
    }

    public static int getAge(String year, String month, String day)
    {
        //set up date of birth
        Calendar calDOB = Calendar.getInstance();
        calDOB.set( Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day) );
        //setup calNow as today.
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(new java.util.Date());
        //calculate age in years.
        int ageYr = (calNow.get(Calendar.YEAR) - calDOB.get(Calendar.YEAR));
        // calculate additional age in months, possibly adjust years.
        int ageMo = (calNow.get(Calendar.MONTH) - calDOB.get(Calendar.MONTH));
        if (ageMo < 0)
        {
            //adjust years by subtracting one
            ageYr--;
        }
        return ageYr;
    }

    public static URL buildUrlProfile(String id_facebook) throws MalformedURLException {
        URL profile_picture = new URL("https://graph.facebook.com/"+ id_facebook+"/picture?width=300&height=300");
        return profile_picture;
    }

    public static void moveChild(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
