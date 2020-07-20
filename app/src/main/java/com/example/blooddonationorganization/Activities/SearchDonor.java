package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.blooddonationorganization.Adapter.DonorAdapter;
import com.example.blooddonationorganization.Adapter.RecyclerAdapter;
import com.example.blooddonationorganization.Model.DonorDataModel;
import com.example.blooddonationorganization.Model.RequestDataModel;
import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchDonor extends AppCompatActivity {

    Spinner donorBlood;
    Spinner donorCity;
    private String record;
    private String record2;
    private ProgressBar dProgressBar;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<DonorDataModel> donorDetailList;
    private RecyclerView recyclerView;
    private DonorAdapter donorAdapter;

    private CollectionReference collectionReferences = db.collection("Donors");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        donorDetailList = new ArrayList<>();

        recyclerView = findViewById(R.id.donor_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dProgressBar = findViewById(R.id.donor_progress_bar);
        dProgressBar.setVisibility(View.VISIBLE);
        donorBlood= (Spinner) findViewById(R.id.d_blood_spinner);
        donorCity = (Spinner) findViewById(R.id.d_city_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SearchDonor.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        donorBlood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        record="Choose Blood Group";
                        break;
                    case 1:
                        record = "A+";
                        break;
                    case 2:
                        record = "A-";
                        break;
                    case 3:
                        record = "B+";
                        break;
                    case 4:
                        record = "B-";
                        break;
                    case 5:
                        record = "AB+";
                        break;
                    case 6:
                        record = "AB-";
                        break;
                    case 7:
                        record = "O+";
                        break;
                    case 8:
                        record = "O-";
                        break;
                }
                getBlood();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        donorBlood.setAdapter(myAdapter);
        ArrayAdapter<String> donorAdapter = new ArrayAdapter<String>(SearchDonor.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.cityNames));
        donorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        donorCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        record2="Choose City";
                        break;
                    case 1:
                        record2 = "Dhaka";
                        break;
                    case 2:
                        record2 = "Chittagong";
                        break;
                    case 3:
                        record2 = "Cumilla";
                        break;
                    case 4:
                        record2 = "Rajshahi";
                        break;
                    case 5:
                        record2 = "Sylhet";
                        break;
                    case 6:
                        record2 = "Rangpur";
                        break;
                    case 7:
                        record2 = "Barishal";
                        break;
                    case 8:
                        record2 = "Chadpur";
                        break;
                    case 9:
                        record2 = "Khulna";
                        break;
                    case 10:
                        record2 = "Mymensingh";
                        break;
                }
                getBlood();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        donorCity.setAdapter(donorAdapter);

    }
    public void getBlood()
    {
        donorDetailList.clear();
        collectionReferences.whereEqualTo("city",record2.toLowerCase()).whereEqualTo("bloodGrp",record)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot donors : queryDocumentSnapshots) {
                                DonorDataModel donor = donors.toObject(DonorDataModel.class);
                                donorDetailList.add(donor);
                            }

                            donorAdapter = new DonorAdapter(SearchDonor.this,
                                    donorDetailList);
                            recyclerView.setAdapter(donorAdapter);
                            donorAdapter.notifyDataSetChanged();

                        }
                        dProgressBar.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DonorsApi donorsApi = DonorsApi.getInstance();
        //record = donorsApi.getBloodGrp();
        record2 = donorsApi.getCity();
            collectionReferences.whereEqualTo("city",record2.toLowerCase())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot donors : queryDocumentSnapshots) {
                                DonorDataModel donor = donors.toObject(DonorDataModel.class);
                                donorDetailList.add(donor);
                            }

                            donorAdapter = new DonorAdapter(SearchDonor.this,
                                    donorDetailList);
                            recyclerView.setAdapter(donorAdapter);
                            donorAdapter.notifyDataSetChanged();

                        }
                        dProgressBar.setVisibility(View.GONE);

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
        Intent intent = new Intent(SearchDonor.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        donorDetailList.clear();
    }
}