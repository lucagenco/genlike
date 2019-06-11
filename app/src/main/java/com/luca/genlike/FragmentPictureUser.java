package com.luca.genlike;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luca.genlike.Utils.Utils;

public class FragmentPictureUser extends Fragment {
    private static ViewPager viewPager;
    ImageAdapter imageAdapter;
    String[] urls = new String[3];
    FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String hUserID = getArguments().getString("userID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(hUserID).child("slot1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urls[0] = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(hUserID).child("slot2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urls[1] = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(hUserID).child("slot3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urls[2] = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        View view = inflater.inflate(R.layout.fragment_picture_user, container, false);
        viewPager = view.findViewById(R.id.pager);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageAdapter = new ImageAdapter(getActivity(), urls);
                viewPager.setAdapter(imageAdapter);
            }
        }, 200);
        return view;
    }
}




