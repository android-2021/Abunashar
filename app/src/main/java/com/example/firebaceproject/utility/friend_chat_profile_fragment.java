package com.example.firebaceproject.utility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.DialogFragment;


import com.example.firebaceproject.R;

public class friend_chat_profile_fragment extends DialogFragment {

    View mview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.friend_chat_profile_fragment,container,false);

        return mview;
    }
}
