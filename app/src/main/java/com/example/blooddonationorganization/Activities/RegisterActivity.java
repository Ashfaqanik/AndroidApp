package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blooddonationorganization.Model.DonorDataModel;
import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    Spinner userCity;
    private String record2;
    private EditText mobileNumber;
    FirebaseFirestore fStore;
    Spinner bloodGrp;
    private String record;
    private EditText password;
    private EditText email;
    private Button submitButton;
    private String UserId;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference donorReference = db.collection("Donors");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        userCity = (Spinner) findViewById(R.id.r_city_spinner);
        mobileNumber = findViewById(R.id.number);
        email = findViewById(R.id.email);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        bloodGrp = (Spinner) findViewById(R.id.spinner);
        progressBar = findViewById(R.id.register_progress_bar);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already loggedin..
                }else {
                    //no user yet...
                }

            }
        };
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bloodGrp.setAdapter(myAdapter);
        ArrayAdapter<String> donorAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.cityNames));
        donorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        userCity.setAdapter(donorAdapter);
        password = findViewById(R.id.password);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String City = record2;
                String Mobile = mobileNumber.getText().toString().trim();
                String bGrp = record;
                String pass = password.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                if(!Name.isEmpty() && !Email.isEmpty() && !City.isEmpty() && !Mobile.isEmpty() && !pass.isEmpty()
                        && !bGrp.equals("Choose Blood Group"))
                {
                    createUser();

                }else
                {
                    showMsg();
                }


            }
        });
    }

    private void createUser() {
        final String username = name.getText().toString();
        final String Email = email.getText().toString().trim();
        final String City = record2.toLowerCase();
        final String Mobile = mobileNumber.getText().toString();
        final String bGrp = record;
        final String pass = password.getText().toString().trim();
        Toast.makeText(this, "Registration in progress...", Toast.LENGTH_SHORT).show();
        firebaseAuth.createUserWithEmailAndPassword(Email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    final String currentUserId = currentUser.getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", currentUserId);
                    user.put("username", username);
                    user.put("email", Email);
                    user.put("city", City.toLowerCase());
                    user.put("mobile", Mobile);
                    user.put("bloodGrp", bGrp);
                    user.put("password", pass);
                    //save to our firestore database
                    collectionReference.document(currentUserId).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();

                                    DonorsApi donorsApi = DonorsApi.getInstance(); //Global API
                                    donorsApi.setUserId(currentUserId);
                                    donorsApi.setUsername(username);
                                    donorsApi.setCity(City);
                                    donorsApi.setBloodGrp(bGrp);
                                    donorsApi.setMobile(Mobile);
                                    donorsApi.setEmail(Email);
                                    donorsApi.setPassword(pass);

                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    intent.putExtra("userId", currentUserId);
                                    intent.putExtra("username", username);
                                    intent.putExtra("city", City.toLowerCase());
                                    startActivity(intent);
                                    RegisterActivity.this.finish();


                                }
                            }).
                            addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this,"User creation not succeed",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    donorReference.document(currentUserId).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });


                } else {
                    Toast.makeText(RegisterActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this,"Registration not succeed",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }


    private void showMsg()
    {
        Toast.makeText(this,"Fill the requirements",Toast.LENGTH_SHORT).show();
    }
}