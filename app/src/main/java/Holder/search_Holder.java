package Holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaceproject.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class search_Holder extends RecyclerView.ViewHolder {

    static View mview;

  public CircleImageView circleImageView;
  final static int request_code = 1;

    public search_Holder(@NonNull View itemView) {
        super(itemView);
        mview=itemView;

    }
    public static void setName(String name){
        TextView tv_name = mview.findViewById(R.id.et_friend);
        tv_name.setText(name);
    }
    public void setStatus(String status){
        TextView tv_status = mview.findViewById(R.id.et_status);
        tv_status.setText(status);
    }
    public void setProfile(Context context,String url){
       CircleImageView circleImageView = mview.findViewById(R.id.iv_friend_image);

        Picasso.with(context).load(url).into(circleImageView);
    }
}
