package com.luca.genlike;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luca.genlike.Controller.ArrayCardsAdapter;
import com.luca.genlike.Controller.Cards;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Controller.SwipeFling;
import com.luca.genlike.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Cards cards_data[];
    private ArrayCardsAdapter cardsAdapter;
    SwipeFlingAdapterView flingContainer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userSex;
    private String oppositeUserSex;
    private String currentUserId;
    List<Cards> rowItems;
    private DatabaseReference usersDB;
    private SessionManager sessionManager;
    //WIDGET
    private Button btnSignOut;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomMenu);
        btnSignOut = findViewById(R.id.logOut);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = mUser.getUid();
        checkUserSex();
        rowItems = new ArrayList<>();
        sessionManager = new SessionManager(MainActivity.this);
        cardsAdapter = new ArrayCardsAdapter(this, R.layout.item, rowItems);

        flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(cardsAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                rowItems.remove(0);
                cardsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                Cards obj = (Cards) o;
                String userId = obj.getUserID();
                usersDB.child(userId).child("connections").child("nope").child(currentUserId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object o) {
                Cards obj = (Cards) o;
                String userId = obj.getUserID();
                usersDB.child(userId).child("connections").child("yeps").child(currentUserId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:bottomNavigationView.setSelectedItemId(1);;
                }

                return true;
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sessionManager.setIsLogged(false);
                Utils.changeActivity(MainActivity.this, LoginActivity.class);
            }
        });
    }

    public void selectRight(View v){
        flingContainer.getTopCardListener().selectRight();
    }

    public void selectLeft(View v){
        flingContainer.getTopCardListener().selectLeft();
    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionDb = usersDB.child(currentUserId).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Utils.debug(MainActivity.this, "new Connection");
                    usersDB.child(currentUserId).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUserId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkUserSex(){
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        usersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("gender").getValue() != null){
                        userSex = dataSnapshot.child("gender").getValue().toString();
                        switch (userSex){
                            case "Male": oppositeUserSex = "Female";break;
                            case "Female" : oppositeUserSex = "Male";break;
                        }
                        getOppositeSex();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getOppositeSex(){
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUserId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUserId) && dataSnapshot.child("gender").getValue().toString().equals(oppositeUserSex)){
                    Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("first_name").getValue().toString(), dataSnapshot.child("id_facebook").getValue().toString(), dataSnapshot.child("age").getValue().toString(), dataSnapshot.child("profile_image").getValue().toString(), dataSnapshot.child("description").getValue().toString()
                    , dataSnapshot.child("latitude").getValue().toString(), dataSnapshot.child("longitude").getValue().toString());
                    rowItems.add(item);
                    cardsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToSettings(View v){
        Utils.changeActivity(MainActivity.this, SettingsActivity.class);
    }

}

