package com.example.firebaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaceproject.Model.ModelChat;
import com.example.firebaceproject.Model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.AdapterChat;

public class friend_chatting_Activity extends AppCompatActivity {
    private static final String TAG = "friend_chatting_Activity";
    ImageView btn_send, profileIV;
    EditText text_send;
    TextView tvName;
    androidx.appcompat.widget.Toolbar mToolbar;
    FirebaseUser fuser;
    FirebaseDatabase mDatabase;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mAuth;
    FirebaseAuth firebaseAuth;
    StorageReference mStorage;
    RecyclerView recyclerView;
    DatabaseReference mReference,userDFRef;
    //for cheking if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    String my_id, userID,friend_id;
    String hisimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chatting_);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        my_id = mAuth.getUid();
        tvName = (TextView) findViewById(R.id.tv_friend_name);
        profileIV = (ImageView) findViewById(R.id.profileIV);
        btn_send = (ImageView) findViewById(R.id.btn_send);
        text_send = (EditText) findViewById(R.id.text_send);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        friend_id = intent.getStringExtra("friend_key");

        //Layout (linearLayout) for Recyclerview

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        /*firebaseAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDFRef = firebaseDatabase.getReference("users");
        Query query = userDFRef.orderByChild("my_id").equalTo(friend_id);*/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDFRef = firebaseDatabase.getReference().child("users");
        //Query query = userDFRef.orderByChild("friend_id").equalTo(friend_id);
         mReference =  userDFRef.child(friend_id);
         mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Log.d(TAG,"message");
                String name = datasnapshot.child(getString(R.string.name)).getValue().toString();
                 hisimage = datasnapshot.child("profile_image").getValue().toString();
                tvName.setText(name);
                Picasso.with(friend_chatting_Activity.this).load(hisimage).into(profileIV);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
              /*btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString().trim();
                if (!msg.equals(msg)) {
                    //sendMessage(mReference,friend_id,msg);
                    Toast.makeText(friend_chatting_Activity.this, "you can't send the message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(msg);

                }
                //text_send.setText("");
            }
        });*/
              btn_send.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      String msg = text_send.getText().toString().trim();
                      if (TextUtils.isEmpty(msg)){
                          //text empty
                          Toast.makeText(friend_chatting_Activity.this, "cannot send message", Toast.LENGTH_SHORT).show();
                      }
                      else{
                          sendMessage(msg);
                      }
                  }
              });
      readMessages();
      //seenMessage();
    }
    /*private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("users");
        seenListener = userRefForSeen.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                if (chat.getReceiver().equals(my_id) && chat.getSender().equals(friend_id)) {
                        HashMap<String, Object> hasSeenHasMap = new HashMap<>();
                        hasSeenHasMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hasSeenHasMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
   /*@Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }*/
    private void readMessages() {
        chatList = new ArrayList<>();
      DatabaseReference userDef = FirebaseDatabase.getInstance().getReference("chats");
       // mReference = FirebaseDatabase.getInstance().getReference(friend_id);
       userDef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                chatList.clear();
               for (DataSnapshot ds : datasnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                if (chat.getReceiver().equals(my_id) && chat.getSender().equals(friend_id) ||
                            chat.getReceiver().equals(friend_id) && chat.getSender().equals(my_id)) {
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(friend_chatting_Activity.this,chatList,hisimage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview;
                    recyclerView.setAdapter(adapterChat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String msg) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //mReference = mDatabase.getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", my_id);
        hashMap.put("receiver", friend_id);
        hashMap.put("message", msg);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("isSeen", false);
        databaseReference.child("chats").push().setValue(hashMap);
        text_send.setText("");
    }

}

