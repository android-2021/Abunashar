package com.example.firebaceproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.firebaceproject.Model.Search_user;
import com.facebook.appevents.ml.Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import Holder.search_Holder;

public class Search_Activity extends AppCompatActivity {
    private static final String TAG = "Search_Activity";

    RecyclerView search_list;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;


    //var
    ImageView iv_search;
    EditText et_search;
    String mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);
        iv_search = (ImageView) findViewById(R.id.user_search);
        et_search = (EditText) findViewById(R.id.et_user_search);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        search_list = findViewById(R.id.rv_search_list);
        search_list.setHasFixedSize(true);
        search_list.setLayoutManager(new LinearLayoutManager(this));
        search_friend();
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearch = et_search.getText().toString();//drop
                //search_friend();
            }
        });

    }

    private void search_friend() {
        Query search_query = mReference .child(getString(R.string.users)).startAt(mSearch)
                .orderByChild(getString(R.string.name))
                .endAt(mSearch+"\uf8ff");
        FirebaseRecyclerAdapter<Search_user,search_Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search_user, search_Holder>(
                Search_user.class,
                R.layout.show_search_friend,
                search_Holder.class,
                search_query

        ) {
            @Override
            protected void populateViewHolder(final search_Holder viewHolder, Search_user Model, int position) {

              final String user_key = getRef(position).getKey();


                viewHolder.setName(Model.getName());
                viewHolder.setStatus(Model.getStatus());
                viewHolder.setProfile(Search_Activity.this,Model.getProfile_image());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"populationViewHolder : Friend uid :-"+ user_key);
                        Intent friend_intent = new Intent(Search_Activity.this,Friend_Profile_Activity.class);
                        friend_intent.putExtra(getString(R.string.friend_add_key),user_key);
                        startActivity(friend_intent);
                    }
                });
            }
        };
        search_list.setAdapter(firebaseRecyclerAdapter);
    }

}
