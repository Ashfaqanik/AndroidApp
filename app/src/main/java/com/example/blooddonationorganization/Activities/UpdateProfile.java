package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    private EditText name;
    private EditText userCity;
    private String record2;
    private EditText mobileNumber;
    FirebaseFirestore fStore;
    private EditText bloodGrp;
    private String record;
    private EditText email;
    private Button saveButton;
    private String UserId;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser User;
    private ProgressBar progressBar;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        User = firebaseAuth.getCurrentUser();
        UserId = User.getUid();
        name = findViewById(R.id.editName);
        userCity = findViewById(R.id.changeCity);
        mobileNumber = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
        bloodGrp = findViewById(R.id.changeBloodGrp);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DonorsApi donorsApi = DonorsApi.getInstance();
        name.setText(donorsApi.getUsername());
        userCity.setText(donorsApi.getCity());
        mobileNumber.setText(donorsApi.getMobile());
        email.setText(donorsApi.getEmail());
        bloodGrp.setText(donorsApi.getBloodGrp());
        saveButton=findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }
    private void updateProfile()
    {
        String Name = name.getText().toString();
        String City = userCity.getText().toString();
        String Mobile = mobileNumber.getText().toString();
        final String Email = email.getText().toString();
        String Blood = bloodGrp.getText().toString();
        final Map<String, Object> user = new HashMap<>();
        user.put("username", Name);
        user.put("email", Email);
        user.put("city", City.toLowerCase());
        user.put("mobile", Mobile);
        user.put("bloodGrp", Blood);
        collectionReference.document(UserId).update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //User.updateEmail(Email);
                Toast.makeText(UpdateProfile.this,"Updated",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateProfile.this, ProfileActivity.class);
                finish();
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }
}