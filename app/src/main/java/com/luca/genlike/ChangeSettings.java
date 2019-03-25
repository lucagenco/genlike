package com.luca.genlike;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChangeSettings extends AppCompatActivity {
    private ImageView profile_image;
    private DatabaseReference dbUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userID;
    private String userSex;
    private NiceSpinner spinnerAttirance;
    private List<String> datasetAttirance;
    private EditText etDescription;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_settings);
        profile_image = findViewById(R.id.change_profile_image);
        spinnerAttirance = findViewById(R.id.spAttirance);
        etDescription = findViewById(R.id.etDescription);

        datasetAttirance = new LinkedList<>(Arrays.asList("Je suis interessé(e) par les hommes", "Je suis interessé(e) par les femmes"));
        spinnerAttirance.attachDataSource(datasetAttirance);

        sessionManager = new SessionManager(ChangeSettings.this);
        userSex = getIntent().getExtras().getString("userSex", "");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userID);
        if(userSex.equals("Male")){
            spinnerAttirance.setSelectedIndex(1);
        }else if(userSex.equals("Female")){
            spinnerAttirance.setSelectedIndex(0);
        }

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    try {
                        Picasso.with(ChangeSettings.this).load(Utils.buildUrlProfile(map.get("id_facebook").toString()).toString()).into(profile_image);
                    } catch (Exception e) {
                        Log.d("YOO", e.getMessage());
                    }
                    if(dataSnapshot.hasChild("description")){
                        etDescription.setText(map.get("description").toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void applyChange(View v){
        if(!etDescription.getText().toString().equals("")){
            DatabaseReference dbInsertDesc = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userID).child("description");
            dbInsertDesc.setValue(etDescription.getText().toString());
        }

        if(spinnerAttirance.getSelectedIndex() == 0){
            //Interessé par les hommes donc est une femme
            if(!userSex.equals("Female")){
                DatabaseReference pathFrom = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userID);
                DatabaseReference pathTo = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(userID);
                Utils.moveChild(pathFrom, pathTo);
                pathFrom.removeValue();
                sessionManager.setSex("Female");
                mAuth.signOut();
                Utils.changeActivity(ChangeSettings.this, LoginActivity.class);
            }
        }
        else if(spinnerAttirance.getSelectedIndex() == 1){
            //Interessé par les femmes donc est un homme
            if(!userSex.equals("Male")){
                DatabaseReference pathFrom = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userID);
                DatabaseReference pathTo = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(userID);
                Utils.moveChild(pathFrom, pathTo);
                pathFrom.removeValue();
                sessionManager.setSex("Male");
                mAuth.signOut();
                Utils.changeActivity(ChangeSettings.this, LoginActivity.class);
            }
        }
        Utils.changeActivity(ChangeSettings.this, MainActivity.class);
    }

    public void pickImageProfile(View v){

    }

    public void changeToDefaultProfile(View v){

    }
}
