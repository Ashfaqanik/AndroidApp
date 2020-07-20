package com.example.blooddonationorganization.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.blooddonationorganization.R;
import com.example.blooddonationorganization.Util.DonorsApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class LayoutDialog extends AppCompatDialogFragment {
    private EditText currentPassword;
    private EditText newPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;

    private CollectionReference documentReference = db.collection("Users");
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Change Password")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Change();

                    }
                });
        currentPassword = view.findViewById(R.id.current_password);
        newPassword = view.findViewById(R.id.new_password);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();
        return builder.create();
    }
    private void Change() {
        String currentPass = currentPassword.getText().toString();
        final String newPass = newPassword.getText().toString();
        DonorsApi donorsApi = DonorsApi.getInstance();
        String oldPass = donorsApi.getPassword();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        final Map<String, Object> User = new HashMap<>();
        User.put("password", newPass);
        if (!currentPass.equals(oldPass)) {
            Toast.makeText(getContext(),"Current Password doesn't match",Toast.LENGTH_LONG).show();
        }
        else if(newPass.equals(oldPass))
        {
            Toast.makeText(getContext(),"current & old password can't be same",Toast.LENGTH_LONG).show();
        }
        else if(newPass.length()<6)
        {
            Toast.makeText(getContext(),"Too short password",Toast.LENGTH_LONG).show();
        }
        else
        {
            documentReference.document(userId).update(User)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_LONG).show();

        }
    }

}
