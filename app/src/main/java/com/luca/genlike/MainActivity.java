package com.luca.genlike;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luca.genlike.Chat.ChatActivity;
import com.luca.genlike.Controller.ArrayCardsAdapter;
import com.luca.genlike.Controller.Cards;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Notification.APIService;
import com.luca.genlike.Notification.Client;
import com.luca.genlike.Notification.Data;
import com.luca.genlike.Notification.MyResponse;
import com.luca.genlike.Notification.Sender;
import com.luca.genlike.Notification.Token;
import com.luca.genlike.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    private ImageView search;
    private APIService apiService;
    //WIDGET

    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomMenu);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = mUser.getUid();
        search = findViewById(R.id.search);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        checkUserSex();
        rowItems = new ArrayList<>();
        sessionManager = new SessionManager(MainActivity.this);
        cardsAdapter = new ArrayCardsAdapter(this, R.layout.item, rowItems);
        bottomNavigationView.setSelectedItemId(R.id.navigation_like);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(rowItems.size() == 0){
                            flingContainer.setVisibility(View.GONE);
                            TextView textView = findViewById(R.id.emptyTV);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
            }

            @Override
            public void onRightCardExit(Object o) {
                Cards obj = (Cards) o;
                String userId = obj.getUserID();
                usersDB.child(userId).child("connections").child("yeps").child(currentUserId).setValue(true);
                isConnectionMatch(userId);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(rowItems.size() == 0){
                            flingContainer.setVisibility(View.GONE);
                            TextView textView = findViewById(R.id.emptyTV);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
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
                    case R.id.navigation_profile:Utils.changeActivity(MainActivity.this, SettingsActivity.class);break;
                    case R.id.navigation_matchs: Utils.changeActivity(MainActivity.this, MatchActivity.class);break;
                }
                return true;
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Cards cards = (Cards)dataObject;
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userID", cards.getUserID());
                bundle.putString("userFirstName", cards.getFirst_name());
                bundle.putString("userBirthday", cards.getBirthday());
                bundle.putString("userLatitude", cards.getLatitude());
                bundle.putString("userLongitude", cards.getLongitude());
                bundle.putString("userDescription", cards.getDescription());
                bundle.putString("userProfileImage", cards.getProfile_image());
                bundle.putString("userIDFacebook", cards.getId_facebook());
                bundle.putString("myLatitude", sessionManager.getLattitude());
                bundle.putString("myLongitude", sessionManager.getLongitude());
                bundle.putString("myUserID", currentUserId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(rowItems.size() == 0){
                    flingContainer.setVisibility(View.GONE);
                    TextView textView = findViewById(R.id.emptyTV);
                    textView.setVisibility(View.VISIBLE);
                    search.setVisibility(View.GONE);
                }
            }
        }, 7000);
    }

    public void selectRight(View v){
        flingContainer.getTopCardListener().selectRight();
    }

    public void selectLeft(View v){
        flingContainer.getTopCardListener().selectLeft();
    }

    public void refresh(View v){
       finish();
       startActivity(getIntent());
    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionDb = usersDB.child(currentUserId).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Utils.debug(MainActivity.this, "new Connection");
                    sendNotification(userId);
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //usersDB.child(currentUserId).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    usersDB.child(currentUserId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    //usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUserId).setValue(true);
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUserId).child("ChatId").setValue(key);
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
                    Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("first_name").getValue().toString(), dataSnapshot.child("id_facebook").getValue().toString(), dataSnapshot.child("birthday").getValue().toString(), dataSnapshot.child("profile_image").getValue().toString(), dataSnapshot.child("description").getValue().toString()
                    , dataSnapshot.child("latitude").getValue().toString(), dataSnapshot.child("longitude").getValue().toString());
                    rowItems.add(item);
                    cardsAdapter.notifyDataSetChanged();
                }
                else{
                    search.setVisibility(View.GONE);
                    flingContainer.setVisibility(View.VISIBLE);
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

    private void sendNotification(final String receiver){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUserId, R.drawable.logo, "Nouveau matchs", "genlike", receiver);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Utils.debug(MainActivity.this, "Failed");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

