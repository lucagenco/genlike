package com.luca.genlike.Controller;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luca.genlike.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SwipeFling implements SwipeFlingAdapterView.onFlingListener {
    private List<Cards> al;
    private ArrayCardsAdapter arrayAdapter;
    private MainActivity mainActivity;
    private DatabaseReference usersDB;

    private int i;

    public SwipeFling(List<Cards> arrayList, ArrayCardsAdapter arrayAdapter, MainActivity context){
        mainActivity = context;
        this.al = arrayList;
        this.arrayAdapter = arrayAdapter;
        i = 0;
    }

    @Override
    public void removeFirstObjectInAdapter() {
        Log.d("LIST", "removed object!");
        al.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit(Object o) {
        Cards obj = (Cards) o;
        String userId = obj.getUserID();

        Toast.makeText(mainActivity, "left", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightCardExit(Object o) {
        Toast.makeText(mainActivity, "right", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdapterAboutToEmpty(int i) {
    }

    @Override
    public void onScroll(float v) {

    }
}
