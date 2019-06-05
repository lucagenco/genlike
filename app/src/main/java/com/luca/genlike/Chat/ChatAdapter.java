package com.luca.genlike.Chat;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luca.genlike.R;
import com.luca.genlike.Utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> ChatList;
    private Context context;

    public ChatAdapter(List<ChatObject> list, Context context){
        this.ChatList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        holder.mMessage.setText(ChatList.get(position).getMessage());
        if(ChatList.get(position).getCurrentUser()){
            holder.mMessage.setGravity(Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#000000"));
            holder.mContainer.setBackgroundColor(Color.parseColor("#dddbdd"));
        }else{
            holder.mMessage.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#dddbdd"));
            holder.mContainer.setBackgroundColor(Color.parseColor("#FF9D9D"));
        }
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    private URL buildUrlProfile(String id_facebook) throws MalformedURLException {
        URL profile_picture = new URL("https://graph.facebook.com/"+ id_facebook+"/picture?width=300&height=300");
        return profile_picture;
    }
}
