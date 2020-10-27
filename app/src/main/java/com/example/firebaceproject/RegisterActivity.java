package com.example.firebaceproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG ="RegisterActivity";
    Button mRegister;
    EditText mUsername,mPassword,mEmail,mName,mCountry;
    CircleImageView profile_image;
    ImageView back_bt;
    String strEmail,strPassword,strUsername,strName,strCountry,strProfileimage;
    Firebase_method firebase_method;
     FirebaseAuth mAuth;
     String userID;
     private static final int request_Code = 5;
     Uri imageUri;


    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
   private FirebaseStorage mfirebasestorage;
    private StorageReference mStorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userID = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
       mfirebasestorage = FirebaseStorage.getInstance();
       mStorage = mfirebasestorage.getReference();
        mRegister =(Button) findViewById(R.id.bt_register);
        mUsername =(EditText)findViewById(R.id.et_username);
        mPassword = (EditText)findViewById(R.id.et_password);
        mEmail = (EditText)findViewById(R.id.et_email);
        mName = (EditText)findViewById(R.id.et_name);
        mCountry = (EditText) findViewById(R.id.et_country);
        profile_image = (CircleImageView)findViewById(R.id.iv_profile_image);
        back_bt = findViewById(R.id.back_activity);
        firebase_method = new Firebase_method(this);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_new_user();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,request_Code);

            }
        });
        setupFirebaseAuthentication();
      //select_Image();
    }

  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code && resultCode == RESULT_OK && data != null && data.getData() != null){
           imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(RegisterActivity.this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
            assert result != null;
            imageUri = result.getUri();
                Log.d(TAG, "onActivityResult: Image Uri" + imageUri.toString());
                profile_image.setImageURI(imageUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
            }
        }
    }
    private void select_Image(){
        if (imageUri!=null){
            StorageReference reference = mStorage.child(getString(R.string.users)).child(userID).child(imageUri.getLastPathSegment());
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                  if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() !=null){
                      Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                      result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                         String image_profile_url = uri.toString();
                         strProfileimage = image_profile_url;
                         mReference.child(getString(R.string.users)).child(userID).child("profile_image").setValue(strProfileimage);
                          }
                      });

                  }
                }
            });

        }

    }

    private void register_new_user() {
        strEmail = mEmail.getText().toString();
        strPassword = mPassword.getText().toString();
        strName = mName.getText().toString();
        strUsername = mUsername.getText().toString();
        strCountry = mCountry.getText().toString();
        if(check_input(strEmail,strPassword)) {
            firebase_method.register_new_email(strEmail, strPassword);
        }

    }
    private boolean check_input(String strEmail,String strPassword){
        if(strEmail.equals("")||strPassword.equals("")){
            Toast.makeText(this, "please required failed", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }
    /*private boolean ifStringNull(String str){
        if (str.equals("")){
            return false;

        }else
           return true;
    }*/

    private void setupFirebaseAuthentication(){
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG,"setupFirebaseAuthentication: ready for send data");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          FirebaseUser user = mAuth.getCurrentUser();
          if(user !=null){
              userID = mAuth.getCurrentUser().getUid();
              Log.d(TAG,"onAuthStatechanged : userID" +userID);
              mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                      firebase_method.send_new_user_data(userID, strName,strEmail,strUsername,strPassword,strCountry,"Delfault");
                   select_Image();
                     Toast.makeText(RegisterActivity.this, "Registration success", Toast.LENGTH_SHORT).show();
                      mAuth.signOut();
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

              finish();
          }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener !=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }
}