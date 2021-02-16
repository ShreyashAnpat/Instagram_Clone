package com.e.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.instagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class comments extends RecyclerView.Adapter<comments.ViewHolder> {
    Context context ;
    List<String> comment , username , dp ;
    LayoutInflater inflater ;

    public comments(Context context, List<String> comment, List<String> username, List<String> dp) {
        this.context = context ;
        this.username = username ;
        this.comment  = comment ;
        this.dp = dp ;
        inflater = LayoutInflater.from(context);
        

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment,parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.comment.setText(comment.get(position));
        Picasso.get().load(dp.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return comment.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment ;
        CircleImageView imageView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
