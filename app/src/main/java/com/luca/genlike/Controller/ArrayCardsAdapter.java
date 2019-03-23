package com.luca.genlike.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luca.genlike.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ArrayCardsAdapter extends ArrayAdapter<Cards> {
    private Context context;

    public ArrayCardsAdapter(Context context, int ressourceID, List<Cards> items){
        super(context, ressourceID, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView txtFirst_name = convertView.findViewById(R.id.name);
        ImageView imgProfile = convertView.findViewById(R.id.profileImage);

        txtFirst_name.setText(card_item.getFirst_name());
        try {
            Picasso.with(this.context).load(buildUrlProfile(card_item.getId_facebook()).toString()).into(imgProfile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private URL buildUrlProfile(String id_facebook) throws MalformedURLException {
        URL profile_picture = new URL("https://graph.facebook.com/"+ id_facebook+"/picture?width=300&height=300");
        return profile_picture;
    }
}
