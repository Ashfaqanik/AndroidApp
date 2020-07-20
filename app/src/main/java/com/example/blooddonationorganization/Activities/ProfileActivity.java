package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationorganization.Adapter.ProfileAdapter;
import com.example.blooddonationorganization.Adapter.RecyclerAdapter;
import com.example.blooddonationorganization.Model.RequestDataModel;
import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private TextView username;
    private TextView userCity;
    private TextView bloodGrp;
    private TextView phone;
    private TextView email;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private List<RequestDataModel> donorList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private ProfileAdapter recyclerAdapter;
    private Button editButton;
    private ProgressBar mProgressBar;
    private String userId;
    private CheckBox permit;

    private CollectionReference collectionReference = db.collection("Requests");
    private CollectionReference documentReference = db.collection("Users");
    private CollectionReference donorReference = db.collection("Donors");
    private LinearLayoutManager LayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        permit = (CheckBox) findViewById(R.id.checkbox_bloodPermit);
        username = findViewById(R.id.userName);
        userCity = findViewById(R.id.userCity);
        bloodGrp = findViewById(R.id.bloodGroup);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        editButton = findViewById(R.id.editButton);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        donorList = new ArrayList<>();
        mProgressBar = findViewById(R.id.profile_progress_bar);

        recyclerView = findViewById(R.id.profile_recyclerview);
        recyclerView.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(ProfileActivity.this);
        LayoutManager.setReverseLayout(true);
        LayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(LayoutManager);
        final DonorsApi donorsApi = DonorsApi.getInstance(); //Global API
        username.setText(MessageFormat.format("Name: {0}", donorsApi.getUsername()));
        userCity.setText(MessageFormat.format("City: {0}", donorsApi.getCity()));
        bloodGrp.setText(MessageFormat.format("Blood group: {0}",donorsApi.getBloodGrp()));
        phone.setText(MessageFormat.format("Contact no: {0}", donorsApi.getMobile()));
        email.setText(MessageFormat.format("Email Address: {0}", donorsApi.getEmail()));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);
                startActivity(intent);
            }
        });

    }
    public void onCheckboxClicked(View view) {
        if(permit.isChecked())
        {
            donorReference.document(userId).delete().
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this,"Removed from donor list",Toast.LENGTH_SHORT).show();
                }
            });
            permit.setChecked(true);
        }else
        {
            final DonorsApi donorsApi = DonorsApi.getInstance();
            String Name = donorsApi.getUsername();
            String City = donorsApi.getCity();
            String Mobile = donorsApi.getMobile();
            String Email = donorsApi.getEmail();
            String Blood = donorsApi.getBloodGrp();
            final Map<String, Object> user = new HashMap<>();
            user.put("username", Name);
            user.put("email", Email);
            user.put("city", City.toLowerCase());
            user.put("mobile", Mobile);
            user.put("bloodGrp", Blood);
            donorReference.document(userId).set(user).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this,"Added as a blood donor",Toast.LENGTH_SHORT).show();
                }
            });
            permit.setChecked(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId",user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot donors : queryDocumentSnapshots) {
                                RequestDataModel donor = donors.toObject(RequestDataModel.class);
                                donorList.add(donor);
                            }

                            recyclerAdapter = new ProfileAdapter(ProfileActivity.this,
                                    donorList);
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerAdapter.notifyDataSetChanged();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        donorList.clear();
    }
}