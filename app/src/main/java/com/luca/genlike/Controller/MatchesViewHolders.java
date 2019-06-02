package com.luca.genlike.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luca.genlike.Chat.ChatActivity;
import com.luca.genlike.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchName;
    public ImageView mImage;
    public TextView mMatchId;
    public MatchesViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchName = itemView.findViewById(R.id.MatchFirstName);
        mImage = itemView.findViewById(R.id.MatchImage);
        mMatchId = itemView.findViewById(R.id.MatchId);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
