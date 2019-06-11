package com.luca.genlike;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luca.genlike.Model.FlatEarthDist;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentInfoUser extends Fragment {
    private DatabaseReference usersDB;
    private String myUserID;
    private String herUserID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_user, container, false);
        ImageView profile_image = view.findViewById(R.id.image_profil);
        TextView nameAndAge = view.findViewById(R.id.ageAndName);
        TextView distance = view.findViewById(R.id.distance);
        TextView description = view.findViewById(R.id.my_description);
        Button buttonLike = view.findViewById(R.id.like);
        Button buttonNope = view.findViewById(R.id.nope);
        String hUserProfileImage = getArguments().getString("userProfileImage");
        String hUserIdFacebook = getArguments().getString("userIdFacebook");
        String hUserBirthday = getArguments().getString("userBirthday");
        String hUserFirstName = getArguments().getString("userFirstName");
        String hUserLatitude = getArguments().getString("userLatitude");
        String hUserLongitude = getArguments().getString("userLongitude");
        String mLongitude = getArguments().getString("myLongitude");
        String mLatitude = getArguments().getString("myLatitude");
        String hDescription = getArguments().getString("userDescription");
        myUserID = getArguments().getString("myUserID");

        herUserID = getArguments().getString("userID");
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        nameAndAge.setText(hUserFirstName + ", " + Utils.displayAge(hUserBirthday));
        double res = FlatEarthDist.distance(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude), Double.parseDouble(hUserLatitude), Double.parseDouble(hUserLongitude));
        int resRound = (int)Math.round(res/1000);
        if(resRound < 1){
            distance.setText("Moins d'un km     ");
        }else{
            distance.setText(resRound + " km     ");
        }

        if(description.equals("")){
            description.setVisibility(View.GONE);
        }else{
            description.setText(hDescription);
        }

        if(hUserProfileImage.equals("facebook_image")){
            try {
                Picasso.with(getActivity()).load(Utils.buildUrlProfile(hUserIdFacebook).toString()).into(profile_image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else{
            Picasso.with(getActivity()).load(hUserProfileImage).into(profile_image);
        }
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersDB.child(herUserID).child("connections").child("yeps").child(myUserID).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.changeActivity(getActivity(), MainActivity.class);
                    }
                });
            }
        });

        buttonNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersDB.child(herUserID).child("connections").child("nope").child(myUserID).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.changeActivity(getActivity(), MainActivity.class);
                    }
                });
            }
        });
        return view;
    }

}
