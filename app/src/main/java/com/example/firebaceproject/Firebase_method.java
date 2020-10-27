package com.example.firebaceproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.firebaceproject.Model.UserData;
import com.example.firebaceproject.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.jar.Attributes;

public class Firebase_method {
    private static final String TAG ="Firebase_method";
    FirebaseAuth mAuth;
    Context mContext;
    String userID;
    DatabaseReference mReference;
    FirebaseDatabase mDatabase;

    public Firebase_method(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mContext = context;

    }
    public void register_new_email(String strEmail,String strPassword) {
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //userID=mAuth.getCurrentUser().getUid();
                            Toast.makeText(mContext, "register success full", Toast.LENGTH_SHORT).show();
                        } else {
                            //userID=mAuth.getCurrentUser().getUid();
                            //Toast.makeText(mContext,"failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    public void send_new_user_data(String userID, String Name, String Email, String Username,String Password,String Country,String profile_image) {
     UserData userData = new UserData(Name, Email, Username, Password,Country,profile_image);
        userID = FirebaseAuth.getInstance().getUid();
        mReference.child("users").child(userID).setValue(userData);

    }
}