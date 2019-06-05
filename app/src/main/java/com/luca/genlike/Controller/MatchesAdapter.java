package com.luca.genlike.Controller;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luca.genlike.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> list, Context context){
        this.matchesList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchName.setText(matchesList.get(position).getFirst_name());
        holder.mMatchId.setText(matchesList.get(position).getUserID());
        if(matchesList.get(position).getProfile_image().equals("facebook_image")){
            try {
                Picasso.with(this.context).load(buildUrlProfile(matchesList.get(position).getId_facebook()).toString()).into(holder.mImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Picasso.with(this.context).load(matchesList.get(position).getProfile_image()).into(holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    private URL buildUrlProfile(String id_facebook) throws MalformedURLException {
        URL profile_picture = new URL("https://graph.facebook.com/"+ id_facebook+"/picture?width=300&height=300");
        return profile_picture;
    }
}
