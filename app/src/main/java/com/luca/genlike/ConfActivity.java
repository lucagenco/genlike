package com.luca.genlike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Utils.Utils;
import com.shawnlin.numberpicker.NumberPicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

public class ConfActivity extends AppCompatActivity {
    private NumberPicker picker_day_birthday;
    private NumberPicker picker_month_birthday;
    private NumberPicker picker_year_birthday;
    private RadioButton radio_homme;
    private RadioButton radio_femme;
    private ImageView profile_image;
    private EditText et_description;
    private TextView tv_first_name;
    private TextView tv_last_name;
    private String userID;
    FirebaseAuth authL;
    private static SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);
        authL = FirebaseAuth.getInstance();
        userID = authL.getCurrentUser().getUid();
        picker_day_birthday = findViewById(R.id.day_birthday);
        picker_month_birthday = findViewById(R.id.month_birthday);
        picker_year_birthday = findViewById(R.id.year_birthday);
        radio_homme = findViewById(R.id.radio_homme);
        radio_femme = findViewById(R.id.radio_femme);
        profile_image = findViewById(R.id.config_profile_image);
        et_description = findViewById(R.id.conf_description);
        tv_first_name =  findViewById(R.id.tv_first_name);
        tv_last_name =  findViewById(R.id.tv_last_name);
        sessionManager = new SessionManager(ConfActivity.this);
        radio_homme.setChecked(true);

        try {
            Picasso.with(ConfActivity.this).load(Utils.buildUrlProfile(sessionManager.getIdFacebook()).toString()).into(profile_image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        tv_last_name.setText(sessionManager.getLastName());
        tv_first_name.setText(sessionManager.getFirstName());

    }

    public void saveNewProfile(View v){
        sessionManager.setIsLogged(true);
        DatabaseReference db_first_name = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("first_name");
        db_first_name.setValue(sessionManager.getFirstName());
        //LAST_NAME
        DatabaseReference db_last_name = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("last_name");
        db_last_name.setValue(sessionManager.getLastName());
        //ID_FACEBOOK
        DatabaseReference db_id_facebook = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("id_facebook");
        db_id_facebook.setValue(sessionManager.getIdFacebook());
        //POSITION
        DatabaseReference db_latitude = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("latitude");
        db_latitude.setValue(sessionManager.getLattitude());
        DatabaseReference db_longitude = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("longitude");
        db_longitude.setValue(sessionManager.getLongitude());
        //PROFILE_IMAGE
        DatabaseReference db_profile_image = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("profile_image");
        db_profile_image.setValue(sessionManager.getProfileImage());

        //SET AGE
        int day = picker_day_birthday.getValue();
        int month = picker_month_birthday.getValue();
        int year = picker_year_birthday.getValue();
        int age = Utils.getAge(year, month, day);
        sessionManager.setDayBirthday(day);
        sessionManager.setMonthBirthday(month);
        sessionManager.setYearBirthday(year);
        DatabaseReference db_age = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("birthday");
        db_age.setValue(sessionManager.getBirthday());
        //SET SEX
        String sex = "";
        if(radio_homme.isChecked()){
            sex = "Male";
        }else{
            sex = "Female";
        }
        //GENDER
        DatabaseReference db_gender = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("gender");
        db_gender.setValue(sex);
        sessionManager.setSex(sex);
        //DESCRIPTION
        DatabaseReference db_description = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("description");
        db_description.setValue(et_description.getText().toString());
        sessionManager.setDescription(et_description.getText().toString());
        Utils.changeActivity(ConfActivity.this, MainActivity.class);
    }
}
