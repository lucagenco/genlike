package com.luca.genlike;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference dbUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userID;
    private String userSex;
    private ImageView profileImage;
    private TextView nameAndAge;
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManager = new SessionManager(SettingsActivity.this);
        profileImage = findViewById(R.id.profile_image);
        nameAndAge = findViewById(R.id.nameAndYear);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        bottomNavigationView = findViewById(R.id.bottomMenu);

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("gender") != null){
                        userSex = map.get("gender").toString();
                    }
                    if(map.get("profile_image").toString().equals("facebook_image")){
                        try {
                            Picasso.with(SettingsActivity.this).load(Utils.buildUrlProfile(map.get("id_facebook").toString()).toString()).into(profileImage);
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }
                    else{
                        try {
                            Picasso.with(SettingsActivity.this).load(map.get("profile_image").toString()).into(profileImage);
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }
                    nameAndAge.setText(map.get("first_name").toString() + ", " + Utils.displayAge(map.get("birthday").toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_like:Utils.changeActivity(SettingsActivity.this, MainActivity.class);break;
                    case R.id.navigation_matchs: Utils.changeActivity(SettingsActivity.this, MatchActivity.class);break;
                }
                return true;
            }
        });

    }

    private URL buildUrlProfile(String id_facebook) throws MalformedURLException {
        URL profile_picture = new URL("https://graph.facebook.com/"+ id_facebook+"/picture?width=300&height=300");
        return profile_picture;
    }

    public void goToChangeSettings(View view){
        Utils.changeActivity(SettingsActivity.this, ChangeSettings.class);
    }

    public void logOut(View v){
        mAuth.signOut();
        sessionManager.setIsLogged(false);
        Utils.changeActivity(SettingsActivity.this, LoginActivity.class);
    }

    public void deleteAccount(View v){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
        builder1.setMessage("Votre compte sera supprimé définitivement");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Confirmer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbUsers.removeValue();
                        mAuth.signOut();
                        sessionManager.deleteAll();
                        Utils.changeActivity(SettingsActivity.this, LoginActivity.class);
                    }
                });

        builder1.setNegativeButton(
                "Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
