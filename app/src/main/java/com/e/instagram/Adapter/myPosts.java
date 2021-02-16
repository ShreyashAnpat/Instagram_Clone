package com.e.instagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.e.instagram.Fragment.user_post;
import com.e.instagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class myPosts extends RecyclerView.Adapter<myPosts.ViewHolder>{
    Context context;
    List<String> images ;
    LayoutInflater inflater ;
    String userID ;
    public myPosts(Context context, List<String> images, String uid) {
        this.context = context ;
        this.images = images ;
        this.inflater = LayoutInflater.from(context);
        this.userID = uid ;
    }

    @NonNull
    @Override
    public myPosts.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = inflater.inflate(R.layout.posts,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myPosts.ViewHolder holder, int position) {
        Picasso.get().load(images.get(position)).into(holder.images);
        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new user_post();
                Bundle bundle = new Bundle();
                bundle.putString("position" , String.valueOf(position) );
                bundle.putString("UID",userID);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
        }
    }
}
