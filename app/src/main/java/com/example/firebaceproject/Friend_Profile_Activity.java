package com.example.firebaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PrivateKey;

public class Friend_Profile_Activity extends AppCompatActivity {
    private static final String TAG = "Friend_Profile_Activity";
    String friend_id, name, status, image, my_id;

    int currentstate = 0;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;

    ImageView profile_image, back;
    TextView tvName, tvStatus;
    Button add_request, cancle_request;
    ImageView chatimageview;
    Context mContext = Friend_Profile_Activity.this;
    String userID;
    ProgressDialog pd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend__profile_);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friend_id = getIntent().getStringExtra("friend_key");
        pd = new ProgressDialog(this);

        profile_image = findViewById(R.id.iv_friend_image);
        back = findViewById(R.id.back_arrow);
        tvName = findViewById(R.id.tv_friend_name);
        tvStatus = findViewById(R.id.tv_friend_status);
        add_request = findViewById(R.id.bt_add_friend);
        chatimageview = (ImageView) findViewById(R.id.chat);
        chatimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Friend_Profile_Activity.this,friend_chatting_Activity.class);
                in.putExtra("friend_key", friend_id);
                startActivity(in);
                //finish();
            }
        });
        cancle_request = findViewById(R.id.bt_cancle_request);
        /*
         * 0 = not friend in list
         * 1=request receiver of friend
         * */
        add_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_friend_request();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onclick Back to MainActivity");
                Pair[] pairs= new Pair[3];
                pairs[0] = new Pair<View,String>(profile_image,"ImageTrans");
                pairs[1] = new Pair<View,String>(tvName,"nameTrans");
                pairs[2] = new Pair<View,String>(tvStatus,"statusTrans");
                finish();
            }
        });
        friend_id = getIntent().getStringExtra(getString(R.string.friend_add_key));
        pd.setMessage("please wait.....");
        pd.show();

        Log.d(TAG, "onCreate: friend user id:-" + friend_id);

    mReference = FirebaseDatabase.getInstance().getReference();

        mReference.child("users").child(friend_id).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                /*String uid = FirebaseAuth.getInstance().getUid();

                name = String.valueOf(dataSnapshot.child(uid).child("name").getValue());
                status = String.valueOf(dataSnapshot.child(uid).child("status").getValue());
                image = String.valueOf(dataSnapshot.child(uid).child("profile_image").getValue());*/
            name = dataSnapshot.child(getString(R.string.name)).getValue().toString();
            status = dataSnapshot.child(getString(R.string.status)).getValue().toString();
            image = dataSnapshot.child("profile_image").getValue().toString();


            tvName.setText(name);
            tvStatus.setText(status);
            Picasso.with(mContext).load(image).into(profile_image);
            pd.dismiss();

            mReference.child(getString(R.string.request)).child(friend_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(my_id)) {
                        String request = dataSnapshot.child(my_id).child(getString(R.string.request_type)).getValue().toString();
                        Log.d(TAG,"Request Type :-"+request);
                        if (request.equals(getString(R.string.received))) {
                            add_request.setEnabled(true);
                            add_request.setText("Cancle Request");
                            add_request.setBackground(getDrawable(R.color.colorPrimaryDark));
                            currentstate = 1;
                        } else
                        if(request.equals(getString(R.string.sent))) {

                            add_request.setEnabled(true);
                            add_request.setText("Accept Request");
                            add_request.setBackground(getDrawable(R.color.com_facebook_blue));
                            currentstate = 2;
                            cancle_request.setVisibility(View.VISIBLE);
                            cancle_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    decline_request();
                                }
                            });
                        }
                    }
                    else {
                        mReference.child(getString(R.string.friend_list)).child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(friend_id)) {
                                    String request = dataSnapshot.child(friend_id).child(getString(R.string.request_type)).getValue().toString();
                                    Log.d(TAG, "Request Type :-" + request);
                                    add_request.setEnabled(true);
                                    add_request.setText("Block");
                                    add_request.setBackground(getDrawable(R.color.colorPrimaryDark));
                                    add_request.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Unfriend();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    });
}

    private void Unfriend() {
        mReference.child(getString(R.string.friend_list)).child(friend_id).child(my_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mReference.child(getString(R.string.friend_list)).child(my_id).child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });
    }
    private void decline_request() {
        mReference.child(getString(R.string.request)).child(friend_id).child(my_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mReference.child(getString(R.string.request)).child(my_id).child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });
    }
    private void add_friend_request() {
        add_request.setEnabled(false);
        if (currentstate == 0) {
            mReference.child(getString(R.string.request)).child(friend_id).child(my_id).child(getString(R.string.request_type))
                    .setValue(getString(R.string.received)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mReference.child(getString(R.string.request)).child(my_id).child(friend_id).child(getString(R.string.request_type))
                            .setValue(getString(R.string.sent)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            add_request.setEnabled(true);
                            add_request.setText("Cancle Request");
                            add_request.setBackground(getDrawable(R.color.colorPrimaryDark));
                            currentstate = 1;
                        }
                    });
                }
            });
        }
        add_request.setEnabled(false);
        if (currentstate == 1) {
            mReference.child(getString(R.string.request)).child(friend_id).child(my_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mReference.child(getString(R.string.request)).child(my_id).child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            add_request.setEnabled(true);
                            add_request.setText("Add Friend Request");
                            add_request.setBackground(getDrawable(R.color.com_facebook_blue));
                            currentstate = 0;
                        }
                    });
                }
            });
        }
        if (currentstate == 2) {

            mReference.child(getString(R.string.friend_list)).child(friend_id).child(my_id).child(getString(R.string.request_type))
                    .setValue(getString(R.string.friend)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mReference.child(getString(R.string.friend_list)).child(my_id).child(friend_id).child(getString(R.string.request_type))
                            .setValue(getString(R.string.friend)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG,"message");

                            mReference.child(getString(R.string.request)).child(friend_id).child(my_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mReference.child(getString(R.string.request)).child(my_id).child(friend_id).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            add_request.setEnabled(true);
                                                            add_request.setText("Block");
                                                            add_request.setBackground(getDrawable(R.color.colorPrimaryDark));
                                                            cancle_request.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
            });


        }
    }
}