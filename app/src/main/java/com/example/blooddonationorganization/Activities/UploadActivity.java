package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationorganization.Model.RequestDataModel;
import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int GALLERY_CODE = 1;
    private static final String TAG = "UploadActivity";
    private EditText descriptionEditText;
    private ImageView chosenImageView;
    private ImageView imageView;
    private ProgressBar uploadProgressBar;
    private Button postButton;
    private String currentUserId;
    private String currentUserName;
    private String currentUserCity;
    private TextView currentCityTextView;
    private TextView currentUserTextView;
    private String currentUserMobile;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private Timestamp time;
    private CollectionReference collectionReference = db.collection("Requests");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.r_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        uploadProgressBar = findViewById(R.id.progress_bar);
        postButton = findViewById(R.id.postButton);
        descriptionEditText = findViewById ( R.id.insert_text);
        chosenImageView = findViewById(R.id.uploadImage);
        imageView = findViewById(R.id.post_imageView);
        currentUserTextView = findViewById(R.id.post_username_textView);
        currentCityTextView = findViewById(R.id.post_userCity_textView);
        chosenImageView.setOnClickListener(this);
        postButton.setOnClickListener(this);

        uploadProgressBar.setVisibility(View.INVISIBLE);
        if (DonorsApi.getInstance() != null) {
            currentUserId = DonorsApi.getInstance().getUserId();
            currentUserName = DonorsApi.getInstance().getUsername();
            currentUserMobile = DonorsApi.getInstance().getMobile();
            currentUserTextView.setText(currentUserName);
            currentUserCity = DonorsApi.getInstance().getCity();
            currentCityTextView.setText(currentUserCity.toUpperCase());

        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }

            }
        };


    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postButton:
                //saveRequest
                saveRequest();
                break;
            case R.id.uploadImage:
                //get image from gallery/phone
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }

    }

    private void saveRequest() {
        final String request = descriptionEditText.getText().toString().trim();

        uploadProgressBar.setVisibility(View.VISIBLE);
        uploadProgressBar.setIndeterminate(true);

        if (!TextUtils.isEmpty(request)
                && imageUri != null) {

            final StorageReference filepath = storageReference //.../request_images/our_image.jpeg
                    .child("request_images")
                    .child("my_image_" + Timestamp.now().getSeconds()); // my_image_887474737

            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadProgressBar.setVisibility(View.VISIBLE);
                                    uploadProgressBar.setIndeterminate(false);
                                    uploadProgressBar.setProgress(0);
                                }
                            }, 300);

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageUrl = uri.toString();
                                    //Todo: create a Journal Object - model
                                    RequestDataModel requestDataModel = new RequestDataModel();
                                    requestDataModel.setCity(currentUserCity);
                                    requestDataModel.setRequest(request);
                                    requestDataModel.setImageUrl(imageUrl);
                                    requestDataModel.setTimeAdded(new Timestamp(new Date()));
                                    time=requestDataModel.getTimeAdded();
                                    requestDataModel.setUsername(currentUserName);
                                    requestDataModel.setUserId(currentUserId);
                                    requestDataModel.setMobile(currentUserMobile);
                                    Toast.makeText(UploadActivity.this, "Request  Upload successful", Toast.LENGTH_LONG).show();
                                    //Todo:invoke our collectionReference
                                    collectionReference.document(String.valueOf(time)).set(requestDataModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    uploadProgressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(UploadActivity.this,
                                                            MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                    //Todo: and save a Journal instance.

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadProgressBar.setVisibility(View.INVISIBLE);

                        }
                    });


        } else {

            uploadProgressBar.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image
                imageView.setImageURI(imageUri);//show image

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
