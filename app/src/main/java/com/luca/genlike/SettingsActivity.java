package com.luca.genlike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    ProgressDialog pd;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pd = new ProgressDialog(SettingsActivity.this);
        pd.setMessage("Chargement de la photo...");
        pd.setCancelable(false);
        pd.show();

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
                            Picasso.with(SettingsActivity.this).load(Utils.buildUrlProfile(map.get("id_facebook").toString()).toString()).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }
                    else{
                        try {
                            Picasso.with(SettingsActivity.this).load(map.get("profile_image").toString()).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }
                    nameAndAge.setText(map.get("first_name").toString() + ", " + map.get("age").toString());
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
}
