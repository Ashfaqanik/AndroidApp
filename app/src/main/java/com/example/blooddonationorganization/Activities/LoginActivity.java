package com.example.blooddonationorganization.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView email;
    private EditText pass;
    private Button loginButton;
    private TextView signUp;
    private TextView resetPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private CheckBox remember;
    private SharedPreferences mPrefs;
    private int attemps=0;
    private static final String PREFS_NAME = "PrefsFile";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPrefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        remember = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        email = findViewById(R.id.Email);
        pass = findViewById(R.id.pass);
        loginButton=findViewById(R.id.login_button);
        getSharedPreferencesData();
        resetPassword = findViewById(R.id.resetPass);
        signUp = findViewById(R.id.sign_UpText);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loginEmailPasswordUser(email.getText().toString().trim(),
                            pass.getText().toString().trim());
            }
        });
    }

    private void getSharedPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_email"))
        {
            String rEmail = sp.getString("pref_email","Not found");
            email.setText(rEmail);
        }
        if(sp.contains("pref_password"))
        {
            String rPass = sp.getString("pref_password","Not found");
            pass.setText(rPass);
        }
        if(sp.contains("pref_checked"))
        {
            Boolean b = sp.getBoolean("pref_checked",false);
            remember.setChecked(b);
        }
    }

    private void showRecoverPasswordDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email Address");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String Email = emailEt.getText().toString().trim();
                beginRecovery(Email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    private void beginRecovery(String email) {
        Toast.makeText(LoginActivity.this,"Sending Email...",Toast.LENGTH_SHORT).show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginEmailPasswordUser(final String Email, final String pwd) {
        if(remember.isChecked())
        {
            Boolean boolIsChecked = remember.isChecked();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("pref_email",Email);
            editor.putString("pref_password",pwd);
            editor.putBoolean("pref_checked",boolIsChecked);
            editor.apply();
        }else
        {
            mPrefs.edit().clear().apply();
        }

            Toast.makeText(this, "Login in progress", Toast.LENGTH_SHORT).show();
            if (!TextUtils.isEmpty(Email)
                    && !TextUtils.isEmpty(pwd)) {
                firebaseAuth.signInWithEmailAndPassword(Email, pwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;
                                    final String currentUserId = user.getUid();
                                    collectionReference.document(currentUserId)
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                    if (e!=null){
                                                        Toast.makeText(LoginActivity.this,"Logged out", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {

                                                        DonorsApi donorsApi = DonorsApi.getInstance();
                                                        assert documentSnapshot != null;
                                                        donorsApi.setUsername(documentSnapshot.getString("username"));
                                                        donorsApi.setUserId(documentSnapshot.getString("userId"));
                                                        donorsApi.setCity(documentSnapshot.getString("city"));
                                                        donorsApi.setBloodGrp(documentSnapshot.getString("bloodGrp"));
                                                        donorsApi.setMobile(documentSnapshot.getString("mobile"));
                                                        donorsApi.setEmail(documentSnapshot.getString("email"));
                                                        donorsApi.setPassword(documentSnapshot.getString("password"));

                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        finish();
                                                        startActivity(intent);
                                                    }


                                                }
                                            });
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this,"Login failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


            } else {

                Toast.makeText(LoginActivity.this,
                        "Please enter email and password",
                        Toast.LENGTH_LONG)
                        .show();
            }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentUser= null;
    }
}
