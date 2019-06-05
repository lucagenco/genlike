package com.luca.genlike;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luca.genlike.Controller.MatchesAdapter;
import com.luca.genlike.Controller.MatchesObject;
import com.luca.genlike.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //RECYCLE VIEW
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDatasetMatches(), MatchActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        getUserMatchId();

        //NAVIGATION
        bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_matchs);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_like:
                        Utils.changeActivity(MatchActivity.this, MainActivity.class);break;
                    case R.id.navigation_profile: Utils.changeActivity(MatchActivity.this, SettingsActivity.class);break;
                }
                return true;
            }
        });

    }

    private void getUserMatchId() {
        DatabaseReference matchDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).child("connections").child("matches");
        matchDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String userID = dataSnapshot.getKey();
                    String first_name = "";
                    String profileImageUrl = "";
                    String id_facebook = "";
                    if(dataSnapshot.child("first_name").getValue() != null){
                        first_name = dataSnapshot.child("first_name").getValue().toString();
                    }
                    if(dataSnapshot.child("profile_image").getValue() != null){
                        profileImageUrl = dataSnapshot.child("profile_image").getValue().toString();
                    }
                    if(dataSnapshot.child("id_facebook").getValue() != null){
                        id_facebook = dataSnapshot.child("id_facebook").getValue().toString();
                    }
                    MatchesObject obj = new MatchesObject(userID, first_name, id_facebook, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();
    private List<MatchesObject> getDatasetMatches() {
        return resultsMatches;
    }


}
