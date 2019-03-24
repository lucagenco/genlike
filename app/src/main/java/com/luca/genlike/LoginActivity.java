package com.luca.genlike;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luca.genlike.Http.MyRequest;
import com.luca.genlike.Model.VolleySingleton;
import com.luca.genlike.Utils.GenderDialog;
import com.luca.genlike.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1; //Used for permissions
    private static final int REQUEST_CODE_LOC = 2;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    private static JSONObject mUser;
    ProgressDialog pd;
    private LocationManager locationManager;
    private static LoginActivity mLoginActivity;
    private static String mLatitude;
    private static String mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginActivity = this;
        LoginManager.getInstance().logOut();
        checkPermissions();
        //GPS
        initLocation();

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_birthday");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            mUser = object;
                            handleFacebookAccessToken(mAccessToken);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.debug(LoginActivity.this, e.getMessage());
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, email, birthday, friends, first_name, last_name, gender, name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Connexion au profil...");
        pd.setCancelable(false);
        pd.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (pd.isShowing()){
                                pd.dismiss();
                            }
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setMessage("Précisez votre attirence. D'autres options seront disponible sur la page profil.");
                            builder1.setCancelable(false);
                            builder1.setPositiveButton(
                                    "Je suis attiré par les femmes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                trtHomme();
                                                dialog.cancel();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Je suis attiré par les hommes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                trtFemme();
                                                dialog.cancel();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Utils.changeActivity(LoginActivity.this, MainActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*PERMISSIONS METHODS*/

    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){

            }
            else {
                requestSendLocationPermission();
            }
        }
        else{
            requestSendInternetPermission();
        }
    }

    public void requestSendInternetPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission non accordé")
                    .setMessage("La permission n'est pas accordé")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.INTERNET}, REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Annuler",  new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.INTERNET}, REQUEST_CODE);
        }
    }

    public void requestSendLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission non accordé")
                    .setMessage("La permission n'est pas accordé")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOC);
                        }
                    })
                    .setNegativeButton("Annuler",  new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOC);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(LoginActivity.this, "Permission accordé", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Permission non accordé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Récupération d'informations...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    @SuppressLint("MissingPermission")
    public void initLocation(){
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Récupération de la position...");
        pd.setCancelable(false);
        pd.show();
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                mLatitude = String.valueOf(location.getLatitude());
                mLongitude = String.valueOf(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    public static void trtHomme() throws JSONException {
        FirebaseAuth authL = FirebaseAuth.getInstance();
        String userID = authL.getCurrentUser().getUid();
        //FIRST_NAME
        DatabaseReference db_first_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("first_name");
        db_first_name.setValue(mUser.getString("first_name"));
        //LAST_NAME
        DatabaseReference db_last_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("last_name");
        db_last_name.setValue(mUser.getString("last_name"));
        //ID_FACEBOOK
        DatabaseReference db_id_facebook = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("id_facebook");
        db_id_facebook.setValue(mUser.getString("id"));
        //YEAR OLD
        String[] birthday = mUser.getString("birthday").split("/");
        int age = Utils.getAge(birthday[2], birthday[1], birthday[0]);
        DatabaseReference db_age = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("age");
        db_age.setValue(age);
        //POSITION
        DatabaseReference db_latitude = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("latitude");
        db_latitude.setValue(mLatitude);
        DatabaseReference db_longitude = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID).child("longitude");
        db_longitude.setValue(mLongitude);
        Utils.changeActivity(mLoginActivity, MainActivity.class);

    }

    public static void trtFemme() throws JSONException {
        FirebaseAuth authL = FirebaseAuth.getInstance();
        String userID = authL.getCurrentUser().getUid();
        //FIRST_NAME
        DatabaseReference db_first_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("first_name");
        db_first_name.setValue(mUser.getString("first_name"));
        //LAST_NAME
        DatabaseReference db_last_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("last_name");
        db_last_name.setValue(mUser.getString("last_name"));
        //ID_FACEBOOK
        DatabaseReference db_id_facebook = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("id_facebook");
        db_id_facebook.setValue(mUser.getString("id"));
        //YEAR OLD
        String[] birthday = mUser.getString("birthday").split("/");
        int age = Utils.getAge(birthday[2], birthday[1], birthday[0]);
        DatabaseReference db_age = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("age");
        db_age.setValue(age);
        //POSITION
        DatabaseReference db_latitude = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("latitude");
        db_latitude.setValue(mLatitude);
        DatabaseReference db_longitude = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID).child("longitude");
        db_longitude.setValue(mLongitude);
        Utils.changeActivity(mLoginActivity, MainActivity.class);

    }

}
