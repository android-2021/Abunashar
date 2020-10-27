package com.example.firebaceproject.Main_window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;

import com.example.firebaceproject.LoginActivity;
import com.example.firebaceproject.MainActivity;
import com.example.firebaceproject.R;
import com.example.firebaceproject.RegisterActivity;
import com.example.firebaceproject.Search_Activity;
import com.example.firebaceproject.account_setting_activity;
import com.example.firebaceproject.utility.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class homeactivity extends AppCompatActivity {
    private static final String TAG = "homeactivity";
    Context mContext = homeactivity.this;
    TabLayout mTabLayout;
    Toolbar mToolbar;
    ViewPager mViewPage;
   // Context mContext;
    SectionPagerAdapter sectionPagerAdapter;
    ImageView mSearch;


  // FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


       mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mViewPage = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(sectionPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPage);
        mSearch = findViewById(R.id.iv_search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(homeactivity.this, Search_Activity.class));
            }
        });
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main_menu,menu);
            return true;
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if((item.getItemId())==R.id.setting_button){
            startActivity(new Intent(mContext, account_setting_activity.class));
        }
        if((item.getItemId())==R.id.logout_button){
            user_logout();
        }

        return true;
    }

    private void user_logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(mContext,MainActivity.class));
        finish();
        Log.d(TAG,"user_logout: User signout");

    }

    @Override
    protected void onStart() {
        super.onStart();
       FirebaseUser user = mAuth.getCurrentUser();
       if (user == null) {
           user_logout();
       }
    }

    @Override
    protected void onStop() {
        super.onStop();

        }
 }

