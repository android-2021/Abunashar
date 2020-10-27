package com.example.firebaceproject.Main_window;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaceproject.Firebase_method;
import com.example.firebaceproject.Friend_Profile_Activity;
import com.example.firebaceproject.Model.Search_user;
import com.example.firebaceproject.R;
import com.example.firebaceproject.utility.friend_chat_profile_fragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

import Holder.search_Holder;

public class Friend_tab  extends Fragment {
    private static final String TAG = "Friend_tab";
    View mview;
    RecyclerView request_list;
    String my_id,name,image,status;

    Firebase_method firebase_method;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference,mFriend_ref,mReceivedRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_friend,container,false);


        mAuth = FirebaseAuth.getInstance();

        my_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child(getString(R.string.friend_list)).child(my_id);
        mFriend_ref = mDatabase.getReference();
        mReceivedRef = mDatabase.getReference();
        request_list = mview.findViewById(R.id.rv_friend_list);
        request_list.setHasFixedSize(true);
        request_list.setLayoutManager(new LinearLayoutManager(getActivity()));


        Request_Friend_list();
        return mview;


    }

    private void Request_Friend_list() {
        FirebaseRecyclerAdapter<Search_user, search_Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search_user, search_Holder>(
                Search_user.class,
                R.layout.show_search_friend,
                search_Holder.class,
                mReference

        ) {
            @Override
            protected void populateViewHolder(final search_Holder viewholder, Search_user search_user, int position) {
                final String    friend_key = getRef(position).getKey();

                                mFriend_ref.child("users").child(friend_key).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //String uid = FirebaseAuth.getInstance().getUid();

                                        name = dataSnapshot.child(getString(R.string.name)).getValue().toString();
                                        status = dataSnapshot.child(getString(R.string.status)).getValue().toString();
                                        image = dataSnapshot.child("profile_image").getValue().toString();
                                        viewholder.setName(name);
                                        viewholder.setStatus(status);
                                        viewholder.setProfile(getContext().getApplicationContext(), image);
                                        Log.d(TAG, "onDatachange: friend_key:-" + friend_key);
                                        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Log.d(TAG, "populationViewHolder : Friend uid :-" + friend_key);
                                                Intent friend_intent = new Intent(getContext(), Friend_Profile_Activity.class);
                                                friend_intent.putExtra(getString(R.string.friend_add_key), friend_key);
                                                startActivity(friend_intent);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                   };
        request_list.setAdapter(firebaseRecyclerAdapter);
    }
}
