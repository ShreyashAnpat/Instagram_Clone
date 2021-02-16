package com.e.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.instagram.R;
import com.e.instagram.messages;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_user extends RecyclerView.Adapter<chat_user.ViewHolder> {
    Context context ;
    LayoutInflater inflater;
    List<String> username , profile ;
    public chat_user(messages messages, List<String> username, List<String> profile) {
       this.context = messages ;
       this.username = username ;
       this.profile = profile ;
       inflater= LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_user_list, parent,false);
        return new chat_user.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(username.get(position));
        Picasso.get().load(profile.get(position)).placeholder(R.drawable.profile).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile ;
        TextView username ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.circleImageView3);
            username = itemView.findViewById(R.id.Username);
        }
    }
}
