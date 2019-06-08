package com.luca.genlike.Utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class Utils {
    public static void changeActivity(Context currentActivity, Class secondActivity){
        Intent intent = new Intent(currentActivity, secondActivity);
        currentActivity.startActivity(intent);
    }

    public static void debug(Context contexte, String message){
        Toast.makeText(contexte,message, Toast.LENGTH_LONG).show();
    }

    public static int getAge(int year, int month, int day)
    {
        //set up date of birth
        Calendar calDOB = Calendar.getInstance();
        calDOB.set( year, month, day );
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

    public static String displayAge(String birthday){
        String[] b = birthday.split("/");
        int age = Utils.getAge(Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2]));
        return String.valueOf(age);
    }

    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("ddd", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("ddd", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("ddd", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("ddd", "Was not able to restart application");
        }
    }
}
