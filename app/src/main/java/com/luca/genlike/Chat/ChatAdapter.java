package com.luca.genlike.Chat;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luca.genlike.R;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> ChatList;
    private FirebaseUser fuser;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;

    public ChatAdapter(List<ChatObject> list, Context context){
        this.ChatList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, null, false);
            return new ChatViewHolders(layoutView);
        }else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, null, false);
            return new ChatViewHolders(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        ChatObject chatObject = ChatList.get(position);
        holder.mMessage.setText(chatObject.getMessage());
        if(chatObject.getProfile_image().equals("facebook_image")){
            try {
                Picasso.with(this.context).load(Utils.buildUrlProfile(chatObject.getIdFacebook()).toString()).into(holder.mProfileImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Picasso.with(this.context).load(chatObject.getProfile_image()).into(holder.mProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(ChatList.get(position).getCurrentUser()){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
