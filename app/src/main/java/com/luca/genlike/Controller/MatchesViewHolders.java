package com.luca.genlike.Controller;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luca.genlike.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchName;
    public ImageView mImage;
    public MatchesViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchName = itemView.findViewById(R.id.MatchFirstName);
        mImage = itemView.findViewById(R.id.MatchImage);
    }
    @Override
    public void onClick(View view) {

    }
}
