package com.example.firebaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebaceproject.Main_window.homeactivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.codeless.RCTCodelessLoggingEventListener;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public  class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;
    SignInButton signInButton;

    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    LoginButton loginbutton;
    CallbackManager mCallbackManager;
    Button mLogin;
    EditText mEmail,mPassword;
    ProgressDialog pd;
    ImageView back;


    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mReference;
    StorageReference mStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);
        signInButton = findViewById(R.id.sign_In_button);
        back = (ImageView) findViewById(R.id.back_activity);
        mAuth = FirebaseAuth.getInstance();
        mLogin = (Button)findViewById(R.id.bt_register);
        mEmail = (EditText)findViewById(R.id.et_email);
        mPassword = (EditText)findViewById(R.id.et_password);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        siginGSO();
       signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //signIn();
                google_sign_in_process();
            }
        });
       //setupFacebook();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailSignIn();

            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user !=null){
                   Log.d(TAG,"OnAuthStateChanged: signed in");
                }
                else
                {
                    Log.d(TAG,"OnAuthStateChanged: signed out");
                }
            }
        };
    }
    private void siginGSO(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    private void google_sign_in_process() {
        Intent signInIntent =Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Log.v(TAG,"Google Sigin Failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG,"firebaseAuthWithGoogle:"+account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mReference.child(getString(R.string.users)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userID = mAuth.getCurrentUser().getUid();
                                    DatabaseReference gDatabase = mReference.child(userID);
                                    gDatabase.child("name").setValue(account.getDisplayName());
                                    gDatabase.child("email").setValue(account.getEmail());
                                    gDatabase.child("username").setValue(account.getGivenName());
                                    gDatabase.child("status").setValue("Available");
                                    //gDatabase.child("user_id").setValue(userID);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            startActivity(new Intent(LoginActivity.this,homeactivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Auth failed", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void emailSignIn() {
        String strEmail,strPassword;
        strEmail = mEmail.getText().toString();
        strPassword = mPassword.getText().toString();
        if (strEmail.equals("")||strPassword.equals("")){
            Toast.makeText(this, "please enter email and password", Toast.LENGTH_SHORT).show();
        }
        else {
            pd.setMessage("please wait.....");
            pd.show();
            mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, homeactivity.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });
        }
    }
   /*private void setupFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        loginbutton = findViewById(R.id.login_button);
       // LoginButton loginButton = mBinding.buttonFacebookLogin;
        loginbutton.setReadPermissions("email", "public_profile");
        loginbutton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
               //handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
// ...
        /*@Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }*/
    }
    /*private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent in = new Intent(LoginActivity.this,RegisterActivity.class);
                            startActivity(in);
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                        // ...
                    }
                });
    }*/
    //google sign in
    /*private void signIn() {

    }*/
   /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
           // updateUI(account);
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }

    }

}*/


