package com.luca.genlike;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentPictureUser extends Fragment {
    private static ViewPager viewPager;
    ImageAdapter imageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_user, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        //imageAdapter = new ImageAdapter(this.getActivity());
        viewPager.setAdapter(imageAdapter);
        return view;
    }
}




