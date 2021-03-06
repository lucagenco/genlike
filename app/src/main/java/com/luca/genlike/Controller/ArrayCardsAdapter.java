package com.luca.genlike.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luca.genlike.ChangeSettings;
import com.luca.genlike.Model.FlatEarthDist;
import com.luca.genlike.R;
import com.luca.genlike.SettingsActivity;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ArrayCardsAdapter extends ArrayAdapter<Cards> {
    private Context context;
    private FlatEarthDist flatEarthDist;
    private SessionManager sessionManager;

    public ArrayCardsAdapter(Context context, int ressourceID, List<Cards> items){
        super(context, ressourceID, items);
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView txtFirst_name = convertView.findViewById(R.id.name);
        ImageView imgProfile = convertView.findViewById(R.id.profileImage);
        TextView txtDescription = convertView.findViewById(R.id.txtDescription);
        TextView txtPosition = convertView.findViewById(R.id.txtPosition);
        String[] positions = sessionManager.getPosition().split(",");
        double res = FlatEarthDist.distance(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(card_item.getLatitude()), Double.parseDouble(card_item.getLongitude()));
        int resRound = (int)Math.round(res/1000);
        if(resRound < 1){
            txtPosition.setText("Moins d'un km     ");
        }else{
            txtPosition.setText(resRound + " km     ");
        }

        txtFirst_name.setText(card_item.getFirst_name() + ", " + Utils.displayAge(card_item.getBirthday()));
        if(card_item.getDescription().equals("")){
            txtDescription.setText("Aucune description...");
        }else{
            txtDescription.setText(card_item.getDescription());
        }
        try {
            if(card_item.getProfile_image().equals("facebook_image")){
                Picasso.with(this.context).load(Utils.buildUrlProfile(card_item.getId_facebook()).toString()).into(imgProfile);
            }
            else{
                Picasso.with(this.context).load(card_item.getProfile_image()).into(imgProfile);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
