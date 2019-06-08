package com.luca.genlike.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.luca.genlike.MatchActivity;
import com.luca.genlike.Notification.APIService;
import com.luca.genlike.Notification.Client;
import com.luca.genlike.Notification.Data;
import com.luca.genlike.Notification.MyResponse;
import com.luca.genlike.Notification.Sender;
import com.luca.genlike.Notification.Token;
import com.luca.genlike.R;
import com.luca.genlike.Utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private Button mSendButton;
    private String currentUID, matchId, chatId;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    private ArrayList<ChatObject> resultsChat = new ArrayList<>();
    private String message = null;
    private String createdByUser = null;
    private String receivedByUser = null;
    private Boolean currentUserBoolean = false;
    private String facebook_id = null;
    private String profile_image = null;
    private String first_name = null;
    private Toolbar toolbar;
    private APIService apiService;
    private boolean notify = false;
    private String my_first_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        matchId = getIntent().getExtras().getString("matchId");
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat =  FirebaseDatabase.getInstance().getReference().child("Chat");
        FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    facebook_id = dataSnapshot.child("id_facebook").getValue().toString();
                    profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    first_name = dataSnapshot.child("first_name").getValue().toString();
                    getChatId();
                    getSupportActionBar().setTitle(first_name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    my_first_name = dataSnapshot.child("first_name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        ((LinearLayoutManager) mChatLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDatasetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
        mSendEditText = findViewById(R.id.myMessage);
        mSendButton = findViewById(R.id.send);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.changeActivity(ChatActivity.this, MatchActivity.class);
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                sendMessage();
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDB = mDatabaseChat.push();
            final Map newMessage = new HashMap();
            newMessage.put("receivedByUser", matchId);
            newMessage.put("createdByUser", currentUID);
            newMessage.put("text", sendMessageText);
            newMessageDB.setValue(newMessage);
            FirebaseDatabase.getInstance().getReference().child("Users").child(newMessage.get("createdByUser").toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        my_first_name = dataSnapshot.child("first_name").getValue().toString();
                        sendNotification(newMessage.get("receivedByUser").toString(), my_first_name, newMessage.get("text").toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if(resultsChat.size() == 0){
                DatabaseReference newMessageDB2 = mDatabaseChat.push();
                Map newMessage2 = new HashMap();
                newMessage2.put("receivedByUser", matchId);
                newMessage2.put("createdByUser", currentUID);
                newMessage2.put("text", sendMessageText);
                newMessageDB2.setValue(newMessage2);
            }
            mSendEditText.setText(null);
        }
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUID, R.drawable.logo, username + ": " + message, "New message", matchId);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Utils.debug(ChatActivity.this, "Failed");
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

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    message = null;
                    createdByUser = null;
                    if(dataSnapshot.child("text").getValue() != null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue() != null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(message != null && createdByUser != null){
                        currentUserBoolean = false;
                        if(createdByUser.equals(currentUID)) {
                            currentUserBoolean = true;
                        }
                    }

                    mChatAdapter = new ChatAdapter(resultsChat, ChatActivity.this);
                    mRecyclerView.setAdapter(mChatAdapter);
                    ChatObject newMessage = new ChatObject(message, currentUserBoolean, facebook_id, profile_image);
                    resultsChat.add(newMessage);
                    mChatAdapter.notifyDataSetChanged();


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
    private List<ChatObject> getDatasetChat() {
        return resultsChat;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUID).setValue(token1);
    }

}
