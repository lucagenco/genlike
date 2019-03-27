package com.luca.genlike;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    ListView listView;
    List<Cards> rowItems;
    private DatabaseReference usersDB;

    //WIDGET
    private Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignOut = findViewById(R.id.logOut);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = mUser.getUid();
        checkUserSex();
        rowItems = new ArrayList<>();

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
                usersDB.child(oppositeUserSex).child(userId).child("connections").child("nope").child(currentUserId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object o) {
                Cards obj = (Cards) o;
                String userId = obj.getUserID();
                usersDB.child(oppositeUserSex).child(userId).child("connections").child("yeps").child(currentUserId).setValue(true);
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
                Utils.changeActivity(MainActivity.this, LoginActivity.class);
            }
        });
    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionDb = usersDB.child(userSex).child(currentUserId).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Utils.debug(MainActivity.this, "new Connection");
                    usersDB.child(userSex).child(currentUserId).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    usersDB.child(oppositeUserSex).child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUserId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkUserSex(){
        DatabaseReference dbMale = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        dbMale.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(mUser.getUid())){
                    userSex = "Male";
                    oppositeUserSex = "Female";
                    getOppositeSex();
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

        DatabaseReference dbFemale = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        dbFemale.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(mUser.getUid())){
                    userSex = "Female";
                    oppositeUserSex = "Male";
                    getOppositeSex();
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

    public void getOppositeSex(){
        DatabaseReference oppositeDbSex = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeDbSex.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUserId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUserId)){
                    if(dataSnapshot.hasChild("profile_image")){
                        Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("first_name").getValue().toString(), dataSnapshot.child("id_facebook").getValue().toString(), dataSnapshot.child("age").getValue().toString(), dataSnapshot.child("profile_image").getValue().toString());
                        rowItems.add(item);
                    }
                    else {
                        Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("first_name").getValue().toString(), dataSnapshot.child("id_facebook").getValue().toString(), dataSnapshot.child("age").getValue().toString(), "facebook_image");
                        rowItems.add(item);
                    }
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
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
    }

}

