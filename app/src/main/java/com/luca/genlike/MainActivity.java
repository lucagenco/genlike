package com.luca.genlike;
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
    ListView listView;
    List<Cards> rowItems;

    //WIDGET
    private Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignOut = findViewById(R.id.logOut);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        checkUserSex();

        rowItems = new ArrayList<>();

        cardsAdapter = new ArrayCardsAdapter(this, R.layout.item, rowItems);

        flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(cardsAdapter);
        flingContainer.setFlingListener(new SwipeFling(rowItems, cardsAdapter, this));


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
                if(dataSnapshot.exists()){
                    Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("first_name").getValue().toString(), dataSnapshot.child("id_facebook").getValue().toString());
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

}

