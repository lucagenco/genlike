package com.luca.genlike;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luca.genlike.Controller.SwipeFling;
import com.luca.genlike.Database.DatabaseManager;
import com.luca.genlike.Utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    SwipeFlingAdapterView flingContainer;
    private DatabaseManager manager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String oppositeGender;

    //WIDGET
    private Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignOut = findViewById(R.id.logOut);
        manager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Utils.debug(MainActivity.this, mUser.getUid());
        //oppositeGender = manager.getOppositeGender();
        //Utils.debug(MainActivity.this, oppositeGender);
        al = new ArrayList<>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");
        al.add("html");
        al.add("c++");
        al.add("css");
        al.add("javascript");

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al);

        flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFling(al, arrayAdapter, this));


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        /*btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.signOut();
                Utils.changeActivity(MainActivity.this, LoginActivity.class);
            }
        });*/
    }

}