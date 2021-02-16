package com.e.instagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.e.instagram.Fragment.profileFragment;
import com.e.instagram.Fragment.viewProfile;
import com.e.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    Context context ;
    LayoutInflater inflater ;
    ArrayList<String> getName, getUsers,imageUri ,UID;
    FirebaseAuth auth ;

    public SearchAdapter(Context context, ArrayList<String> getName, ArrayList<String> getUsers, ArrayList<String> imageUri, ArrayList<String> UID) {
       this.context = context;
       this.inflater = LayoutInflater.from(context);
       this.getName = getName ;
       this.getUsers = getUsers ;
       this.imageUri = imageUri ;
       this.UID = UID ;
        auth = FirebaseAuth.getInstance() ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.user_card,parent,false);
        return new SearchAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Name.setText(getName.get(position));
        holder.Username.setText(getUsers.get(position));
        Picasso.get().load(imageUri.get(position)).into(holder.profile);
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String us = UID.get(position);

                if (us.equals(auth.getCurrentUser().getUid()) ){

                    Fragment  fragments = new profileFragment();
                    AppCompatActivity activity1 = (AppCompatActivity)v.getContext();
                    activity1.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments).commit();
                }else {
                    Fragment fragment = new viewProfile();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putString("Users", UID.get(position));
                    bundle.putString("Profile", imageUri.get(position));
                    bundle.putString("Username", getUsers.get(position));
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return getName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile ;
        TextView Username , Name ;
        ConstraintLayout user ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user);
            profile = itemView.findViewById(R.id.circleImageView2);
            Username = itemView.findViewById(R.id.textView);
            Name = itemView.findViewById(R.id.Name);
        }
    }
}
