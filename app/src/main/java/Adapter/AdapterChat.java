package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaceproject.Model.ModelChat;
import com.example.firebaceproject.R;
import com.example.firebaceproject.friend_chatting_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{
    private static final int MSG_TYPE_LEFT =0;
    private static final int MSG_TYPE_RIGHT =1;
    Context mcontext;
    List<ModelChat> chatList;
    String imageUri,friend_id;
    FirebaseUser mAuth;
    String timestamp;
    ImageView profileIV;

    /*FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mReference;
    StorageReference mStorage;
    FirebaseAuth.AuthStateListener mAuthListener;*/
    public AdapterChat(Context mcontext, List<ModelChat> chatList, String imageUri) {
        this.mcontext = mcontext;
        this.chatList = chatList;
       this.imageUri = imageUri;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout: item left side.xml for receiver, or Right
        if (i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,viewGroup,false);
            return new AdapterChat.MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new AdapterChat.MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, int i) {
        //get data
        final String friend_id = String.valueOf(chatList.get(i));
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getTimestamp();

        //convert time stamp to dd/mm/yyyy/ hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        //set data
        myholder.messageTv.setText(message);
        myholder.timeTv.setText(dateTime);
        //click to show delete dialog

        myholder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to this message");
                // button delete
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteMessage(i);

                    }
                });
                //cancle delete  button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        //dismiss dialog
                        dialog.dismiss();

                    }
                });
                //create and show dialog
                builder.create().show();
            }
        });

        //Picasso.with(mcontext).load(imageUri).into(profileIV);
        myholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, friend_chatting_Activity.class);
                intent.putExtra("friend_key",friend_id);
                mcontext.startActivity(intent);
            }
        });
       try{
            Picasso.with(mcontext).load(imageUri).into(myholder.profileIV);
        }
        catch (Exception e){

        }


        //set seen/delivered status of message
        if (i == chatList.size()-1){
            if (chatList.get(i).isSeen()){
                myholder.isSeenTv.setText("Seen");
            }
            else
            {
                myholder.isSeenTv.setText("Delivered");
            }
        }
        else{
            myholder.isSeenTv.setVisibility(View.GONE);
        }


    }

    private void deleteMessage(int position) {
        final String my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /* Logic
        * Get timestamp of the clicked message with all message in chats
        * where both values matches delete message*/
        String msgtimestamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgtimestamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds:datasnapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(my_id)){

                        /*we can do one two things here
                         *Remove the message from chat
                         * set the value of message "This message was deleted..."
                         * so do whatever you want */

                        //Remove the message from chats
                        //ds.getRef().removeValue();

                        //set the value message "This message was deleted..."
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("message","This message was deleted...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(mcontext, "message deleted...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mcontext, "You can delete only your message...", Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently user
        mAuth  = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(mAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

         ImageView profileIV;
        TextView messageTv,timeTv,isSeenTv,name;
        LinearLayout messageLayout;   //click listn to show delete

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv);
            profileIV = itemView.findViewById(R.id.profileIV);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            name = itemView.findViewById(R.id.tv_friend_name);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

}
