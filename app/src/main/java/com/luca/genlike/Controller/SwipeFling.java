package com.luca.genlike.Controller;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luca.genlike.MainActivity;

import java.util.ArrayList;

public class SwipeFling implements SwipeFlingAdapterView.onFlingListener {
    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private MainActivity mainActivity;

    private int i;

    public SwipeFling(ArrayList<String> arrayList, ArrayAdapter<String> arrayAdapter, MainActivity context){
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
