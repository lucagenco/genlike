package com.luca.genlike;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luca.genlike.Chat.ChatActivity;
import com.luca.genlike.Controller.SessionManager;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.angmarch.views.NiceSpinner;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChangeSettings extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_PICk = 2;
    public static final int REQUEST_CODE_PICK_SLOT1 = 3;
    public static final int REQUEST_CODE_PICK_SLOT2 = 4;
    public static final int REQUEST_CODE_PICK_SLOT3 = 5;
    private ImageView profile_image;
    private DatabaseReference dbUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userID;
    private String userSex;
    private NiceSpinner spinnerAttirance;
    private List<String> datasetAttirance;
    private EditText etDescription;
    private SessionManager sessionManager;
    private Uri resultUri;
    private Uri slot1tUri;
    private Uri slot2tUri;
    private Uri slot3tUri;
    ProgressDialog pd;
    ProgressDialog pd2;
    private ImageView slot1;
    private ImageView slot2;
    private ImageView slot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_settings);
        profile_image = findViewById(R.id.change_profile_image);
        spinnerAttirance = findViewById(R.id.spAttirance);
        etDescription = findViewById(R.id.etDescription);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        resultUri = null;
        pd = new ProgressDialog(ChangeSettings.this);
        pd.setMessage("Chargement des données...");
        pd.setCancelable(true);
        pd.show();
        pd2 = new ProgressDialog(ChangeSettings.this);
        pd2.setMessage("Validation des données...");
        pd2.setCancelable(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Réglages");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.changeActivity(ChangeSettings.this, SettingsActivity.class);
            }
        });

        datasetAttirance = new LinkedList<>(Arrays.asList("Je suis interessé(e) par les hommes", "Je suis interessé(e) par les femmes"));
        spinnerAttirance.attachDataSource(datasetAttirance);

        sessionManager = new SessionManager(ChangeSettings.this);
        userSex = sessionManager.getSex();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        if(userSex.equals("Male")){
            spinnerAttirance.setSelectedIndex(1);
        }else if(userSex.equals("Female")){
            spinnerAttirance.setSelectedIndex(0);
        }

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("profile_image").toString().equals("facebook_image")){
                        try {
                            Picasso.with(ChangeSettings.this).load(Utils.buildUrlProfile(map.get("id_facebook").toString()).toString()).into(profile_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }
                    else{
                        try {
                            Picasso.with(ChangeSettings.this).load(map.get("profile_image").toString()).into(profile_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } catch (Exception e) {
                            Log.d("YOO", e.getMessage());
                        }
                    }

                    if(dataSnapshot.hasChild("description")){
                        etDescription.setText(map.get("description").toString());
                    }

                    Picasso.with(ChangeSettings.this).load(map.get("slot1").toString()).into(slot1);
                    Picasso.with(ChangeSettings.this).load(map.get("slot2").toString()).into(slot2);
                    Picasso.with(ChangeSettings.this).load(map.get("slot3").toString()).into(slot3);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void applyChange(View v){
        pd2.show();
        if(!etDescription.getText().toString().equals("")){
            DatabaseReference dbInsertDesc = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("description");
            dbInsertDesc.setValue(etDescription.getText().toString());
        }

        if(spinnerAttirance.getSelectedIndex() == 0){
            //Interessé par les hommes donc est une femme
            if(!userSex.equals("Female")){
                DatabaseReference pathFrom = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("gender");
                pathFrom.setValue("Female");
                sessionManager.setSex("Female");
                /*mAuth.signOut();
                sessionManager.setIsLogged(false);*/
                Utils.changeActivity(ChangeSettings.this, MainActivity.class);
            }
        }
        else if(spinnerAttirance.getSelectedIndex() == 1){
            //Interessé par les femmes donc est un homme
            if(!userSex.equals("Male")){
                DatabaseReference pathFrom = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("gender");
                pathFrom.setValue("Male");
                sessionManager.setSex("Male");
                /*mAuth.signOut();
                sessionManager.setIsLogged(false);*/
                Utils.changeActivity(ChangeSettings.this, MainActivity.class);
            }
        }

        if(resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bit = null;
            try {
                bit = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dbUsers.child("profile_image").setValue(uri.toString());
                        }
                    });
                }
            });
        }else if(slot1tUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("pictures").child(userID+"slot1");
            Bitmap bit = null;
            try {
                bit = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), slot1tUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dbUsers.child("slot1").setValue(uri.toString());
                        }
                    });
                }
            });
        }else if(slot2tUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("pictures").child(userID+"slot2");
            Bitmap bit = null;
            try {
                bit = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), slot2tUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dbUsers.child("slot2").setValue(uri.toString());
                        }
                    });
                }
            });
        }else if(slot3tUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("pictures").child(userID+"slot3");
            Bitmap bit = null;
            try {
                bit = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), slot3tUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dbUsers.child("slot3").setValue(uri.toString());
                        }
                    });
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pd2.isShowing()){
                    pd2.dismiss();
                }
                Utils.changeActivity(ChangeSettings.this, MainActivity.class);
            }
        }, 500);
    }

    public void pickImageProfile(View v){
        if(ContextCompat.checkSelfPermission(ChangeSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICk);
        }
        else {
            requestSendStoragePermission();
        }
    }

    public void pickSlot1(View v){
        if(ContextCompat.checkSelfPermission(ChangeSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_SLOT1);
        }
        else {
            requestSendStoragePermission();
        }
    }

    public void pickSlot2(View v){
        if(ContextCompat.checkSelfPermission(ChangeSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_SLOT2);
        }
        else {
            requestSendStoragePermission();
        }
    }

    public void pickSlot3(View v){
        if(ContextCompat.checkSelfPermission(ChangeSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_SLOT3);
        }
        else {
            requestSendStoragePermission();
        }
    }

    public void changeToDefaultProfile(View v){
        dbUsers.child("profile_image").setValue("facebook_image");
        Utils.changeActivity(ChangeSettings.this, MainActivity.class);
    }

    public void requestSendStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission non accordé")
                    .setMessage("La permission n'est pas accordé")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(ChangeSettings.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Annuler",  new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(ChangeSettings.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PICk && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            profile_image.setImageURI(imageUri);
            resultUri = imageUri;
        }else if(requestCode == REQUEST_CODE_PICK_SLOT1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            slot1.setImageURI(imageUri);
            slot1tUri = imageUri;
        }else if(requestCode == REQUEST_CODE_PICK_SLOT2 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            slot2.setImageURI(imageUri);
            slot2tUri = imageUri;
        }else if(requestCode == REQUEST_CODE_PICK_SLOT3 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            slot3.setImageURI(imageUri);
            slot3tUri = imageUri;
        }
    }
}
