package com.luca.genlike;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static SessionManager sessionManager;
    private static String mUserSex;
    private boolean checkLocService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(LoginActivity.this);
        callbackManager = CallbackManager.Factory.create();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginActivity = this;
        LoginManager.getInstance().logOut();
        checkPermissions();
        statusCheck();
        //GPS

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

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
                parameters.putString("fields", "id, email, first_name, last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Utils.debug(LoginActivity.this, "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Utils.debug(LoginActivity.this, error.getMessage());
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
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if(sessionManager.getSex().equals("")){
                                //FIRST CONNEXION
                                try {
                                    sessionManager.setIdFacebook(mUser.getString("id"));
                                    sessionManager.setProfileImage("facebook_image");
                                    sessionManager.setFirstName(mUser.getString("first_name"));
                                    sessionManager.setLastName(mUser.getString("last_name"));
                                    sessionManager.setPosition(mLatitude, mLongitude);
                                    statusCheck();
                                    if(checkLocService){
                                        Utils.changeActivity(LoginActivity.this, ConfActivity.class);
                                    }else{
                                        statusCheck();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                try{
                                    mUserSex = sessionManager.getSex();
                                    sessionManager.setIsLogged(true);
                                    sessionManager.setPosition(mLatitude, mLongitude);
                                    trtConnexion();
                                }
                                catch(Exception e){
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && sessionManager.isLogged()){
            sessionManager.setIsLogged(true);
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

    @SuppressLint("MissingPermission")
    public void initLocation(){
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Récupération de la position...");
        pd.setCancelable(false);
        pd.show();
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                mLatitude = String.valueOf(location.getLatitude());
                mLongitude = String.valueOf(location.getLongitude());
                checkLocService = true;
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
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1000, locationListener);
    }

    public static void trtConnexion() throws JSONException {
        FirebaseAuth authL = FirebaseAuth.getInstance();
        String userID = authL.getCurrentUser().getUid();

        /*FACEBOOK INFO*/

        //FIRST_NAME
        DatabaseReference db_first_name = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("first_name");
        db_first_name.setValue(mUser.getString("first_name"));
        //LAST_NAME
        DatabaseReference db_last_name = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("last_name");
        db_last_name.setValue(mUser.getString("last_name"));
        //ID_FACEBOOK
        DatabaseReference db_id_facebook = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("id_facebook");
        db_id_facebook.setValue(mUser.getString("id"));

        /*--------------*/
        /*GENLIKE INFO*/

        //YEAR OLD
        DatabaseReference db_age = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("birthday");
        db_age.setValue(sessionManager.getBirthday());
        //POSITION
        DatabaseReference db_latitude = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("latitude");
        db_latitude.setValue(mLatitude);
        DatabaseReference db_longitude = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("longitude");
        db_longitude.setValue(mLongitude);
        //GENDER
        DatabaseReference db_gender = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("gender");
        db_gender.setValue(mUserSex);

        //DESCRIPTION
        DatabaseReference db_description = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("description");
        db_description.setValue(sessionManager.getDescription());
        Utils.changeActivity(mLoginActivity, MainActivity.class);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            checkLocService = false;
            buildAlertMessageNoGps();
        }else{
            initLocation();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre localisation est désactivé. Celle-ci est obligatoire pour que l'application fonctionne. L'application nécessite un redémarrage une fois la localisation activé.")
                .setCancelable(false)
                .setPositiveButton("Activer", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Désactiver", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
