package com.example.firebaceproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.jar.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;

public class account_setting_activity extends AppCompatActivity {
    private static final String TAG = "account_setting_activity";

    ImageView mback, mupdate;
    CircleImageView mprofile_image;
    EditText mName, mUsername, mMobile, mCountry, mStatus;
    TextView mChange_image;

    Firebase_method firebase_method;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;

    private static final int Gallery_request = 1;
    Context mContext = account_setting_activity.this;
    String userID;
    String name, username, country, status, phone;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting_activity);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        firebase_method = new Firebase_method(this);
        userID = FirebaseAuth.getInstance().getUid();


        mback = findViewById(R.id.back_arrow);
        mupdate = findViewById(R.id.Update_done);
        mStatus = (EditText) findViewById(R.id.etStatus);
        mName = (EditText) findViewById(R.id.etName);
        mUsername = (EditText) findViewById(R.id.etUsername);
        mCountry = (EditText) findViewById(R.id.etCountry);
        mMobile = (EditText) findViewById(R.id.etMobile);
        mprofile_image = (CircleImageView) findViewById(R.id.iv_profile_image);
        mChange_image = (TextView) findViewById(R.id.change_profile_image);
        getUser_profile_Data();
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick Back to MainActivity");
                finish();
            }
        });
        mupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_User_profile_data();
            }
        });
        mprofile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_request);

            }
        });
        mChange_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_request);
            }
        });
    }

    // this method used for updata of data
    private void set_User_profile_data() {
        //final String name, username, country, status, phone;
        name = mName.getText().toString();
        username = mUsername.getText().toString();
        country = mCountry.getText().toString();
        status = mStatus.getText().toString();
        phone = mMobile.getText().toString();
        mReference.child(getString(R.string.users)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("message",name);
                mReference.child(getString(R.string.users)).child(userID).child(getString(R.string.name)).setValue(name);
                mReference.child(getString(R.string.users)).child(userID).child(getString(R.string.username)).setValue(username);
                mReference.child(getString(R.string.users)).child(userID).child(getString(R.string.country)).setValue(country);
                mReference.child(getString(R.string.users)).child(userID).child(getString(R.string.status)).setValue(status);
                mReference.child(getString(R.string.users)).child(userID).child(getString(R.string.phone)).setValue(phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //this method used for fetch of data
    private void getUser_profile_Data() {
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange:User id:-"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                String name, username, country, image, status,phone;

//datasnapshot = this method are used for get of value
                //databasebase = this method are used for data stored

               String uid = FirebaseAuth.getInstance().getUid();
                //Log.e("message", uid);
                name = String.valueOf(dataSnapshot.child(uid).child("name").getValue());
                username = String.valueOf(dataSnapshot.child(uid).child("username").getValue());
               country = String.valueOf(dataSnapshot.child(uid).child("country").getValue());
                image = String.valueOf(dataSnapshot.child(uid).child("profile_image").getValue());
                status = String.valueOf(dataSnapshot.child(uid).child("status").getValue());
                phone = String.valueOf(dataSnapshot.child(uid).child("phone").getValue());

                //Log.e("children", name);
                //username = dataSnapshot.child(getString(R.string.username)).getValue().toString();
                //country = dataSnapshot.child(getString(R.string.country)).getValue().toString();
                //image = dataSnapshot.child(getString(R.string.profile_image)).getValue().toString();
                // status = dataSnapshot.child(getString(R.string.status)).getValue().toString();

                mName.setText(name);
                mUsername.setText(username);
                 mCountry.setText(country);
                mStatus.setText(status);
                 mMobile.setText(phone);
                Picasso.with(mContext).load(image).into(mprofile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_request && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(account_setting_activity.this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                Log.d(TAG, "onActivityResult: Image Uri" + imageUri.toString());
                mprofile_image.setImageURI(imageUri);

                StorageReference reference = mStorage.child(getString(R.string.users)).child(userID).child("profile.jpg");
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() !=null){
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image_profile_url = uri.toString();
                                    mReference.child(getString(R.string.users)).child(userID).child("profile_image").setValue(image_profile_url);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(account_setting_activity.this, "profile image not upload", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
