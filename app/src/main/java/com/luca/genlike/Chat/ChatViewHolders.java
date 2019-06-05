package com.luca.genlike.Chat;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luca.genlike.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMessage;
    public LinearLayout mContainer;
    public ChatViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
    }
    @Override
    public void onClick(View view) {

    }
}
