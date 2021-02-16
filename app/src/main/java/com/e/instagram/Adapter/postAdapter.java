package com.e.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.e.instagram.Fragment.Comment_Fragment;
import com.e.instagram.Fragment.profileFragment;
import com.e.instagram.Fragment.user_post;
import com.e.instagram.Fragment.viewProfile;
import com.e.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class postAdapter  extends RecyclerView.Adapter<postAdapter.ViewHolder>{

    private Context context;
    private LayoutInflater layoutInflater ;
    private List<String> postImage;
    private List<String> imageCaption;
    private List<String> userName;
    private List<String> image ,UID , location;
    FirebaseFirestore db ;
    FirebaseAuth auth  ;
    int i =0 ;
    String currentUID ;

    public postAdapter(Context context, List<String> postImage, List<String> imageCaption, List<String> userName, List<String> image , List<String> UID , List<String> location ) {
        this.postImage = new ArrayList<>();
        this.imageCaption = new ArrayList<>();
        this.userName = new ArrayList<>();
        this.image = new ArrayList<>();
        this.context = context ;
        this.layoutInflater =LayoutInflater.from(context);
        this.postImage = postImage ;
        this.imageCaption =imageCaption ;
        this.userName = userName ;
        this.image = image ;
        this.UID = UID ;
        this.location =location ;
        db = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        this.currentUID = auth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public postAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =layoutInflater.inflate(R.layout.activity_poast,parent,false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull postAdapter.ViewHolder holder, int position) {
        holder.username.setText(userName.get(position));
        holder.location.setText(location.get(position));
        if (holder.location.getText() ==""){
            holder.location.setHeight(0);
        }
        Picasso.get().load(postImage.get(position)).into(holder.post);
        Picasso.get().load(image.get(position)).placeholder(R.drawable.profile).into(holder.circleImageView);
        holder.caption.setText(imageCaption.get(position));
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String , Object> save = new HashMap();
                save.put("Username",userName.get(position));
                save.put("Profile", image.get(position));
                save.put("post",postImage.get(position));
                save.put("Saved","true");
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Save").document().set(save);
                holder.saved.setVisibility(View.VISIBLE);
                holder.save.setVisibility(View.INVISIBLE);
            }
        });

        holder.saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Save").whereEqualTo("post",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc :task.getResult()){
                            db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Save").document(doc.getId()).delete();
                            holder.saved.setVisibility(View.INVISIBLE);
                            holder.save.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Save").whereEqualTo("post",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()){

                    for (QueryDocumentSnapshot doc :task.getResult()){
                        String isSave = doc.get("post").toString();
                        if (isSave != postImage.get(position)){
                            holder.saved.setVisibility(View.VISIBLE);
                            holder.save.setVisibility(View.INVISIBLE);
//                        Toast.makeText(context, "available", Toast.LENGTH_SHORT).show();
                        }else {
                            holder.saved.setVisibility(View.INVISIBLE);
                            holder.save.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });

        holder.unLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                HashMap<String ,Object> map = new HashMap<>();
                map.put("Post" , postImage.get(position));
                map.put("username",userName.get(position));
                map.put("profile", image.get(position));
                holder.unLick.setVisibility(View.INVISIBLE);
                holder.lick.setVisibility(View.VISIBLE);
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("licked post").document().set(map);
                HashMap<String , Object> like = new HashMap<>();
                like.put("liked user" , auth.getCurrentUser().getUid());
                db.collection("Poast").whereEqualTo("Image",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                db.collection("Poast").document(doc.getId()).collection("likes").document().set(like);

                            }
                        }
                    }
                });
            }
        });
        holder.lick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("licked post").whereEqualTo("Post",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()){
                            for (QueryDocumentSnapshot doc : task.getResult()){

                                db.collection("Poast").whereEqualTo("Image" ,postImage.get(position)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()){
                                            if (documentSnapshot.getType()== DocumentChange.Type.ADDED){
                                                db.collection("Poast").document(documentSnapshot.getDocument().getId()).collection("likes").whereEqualTo("liked user",auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (DocumentSnapshot docs : queryDocumentSnapshots){
                                                            db.collection("Poast").document(documentSnapshot.getDocument().getId()).collection("likes").document(docs.getId()).delete() ;

                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });

                                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("licked post").document(doc.getId()).delete();
                                holder.lick.setVisibility(View.INVISIBLE);
                                holder.unLick.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });

        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("licked post").whereEqualTo("Post",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        String isLick = doc.get("Post").toString();
                        if (isLick != postImage.get(position) ){
                            holder.lick.setVisibility(View.VISIBLE);
                            holder.unLick.setVisibility(View.INVISIBLE);
                        }else   {

                            holder.lick.setVisibility(View.INVISIBLE);
                            holder.unLick.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        db.collection("Poast").whereEqualTo("Image",postImage.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()){
//                    Toast.makeText(context, doc.getId(), Toast.LENGTH_SHORT).show();
                    String document = doc.getId();
                    List<String > liked_users = new ArrayList<>();

                    db.collection("Poast").document(document).collection("likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            holder.lick_Count.setText( value.size()+ " likes");
                        }
                    });


                }

            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment  fragment = new Comment_Fragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("PostImage" , postImage.get(position));
                bundle.putString("Username", userName.get(position));
                bundle.putString("userDP",image.get(position));
                bundle.putString("PostCaption", imageCaption.get(position));
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
        String UIDS = UID.get(position);
        holder.openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String us = UID.get(position);

                if (us.equals(currentUID) ){

                    Fragment  fragments = new profileFragment();
                    AppCompatActivity activity1 = (AppCompatActivity)v.getContext();
                    activity1.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments).commit();
                }
                else {
                    Fragment  fragment = new viewProfile();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putString("Users", UID.get(position));
                    bundle.putString("Profile", image.get(position));
                    bundle.putString("Username", userName.get(position));
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return postImage.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username ,caption , lick_Count ,location ;
        LottieAnimationView lick ;
        CircleImageView circleImageView ;
        ImageView post ,save,saved  , unLick , share , comment ,licked ,options;
        LottieAnimationView animationView  ;
        LinearLayout openProfile ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_names);
            post = itemView.findViewById(R.id.post);
            circleImageView = itemView.findViewById(R.id.profile_image);
            animationView = itemView.findViewById(R.id.lodder);
            caption = itemView.findViewById(R.id.caption);
            save = itemView.findViewById(R.id.save);
            saved = itemView.findViewById(R.id.saved);
            lick = itemView.findViewById(R.id.lick);
            unLick = itemView.findViewById(R.id.unLick);
            lick_Count = itemView.findViewById(R.id.lick_count);
            share = itemView.findViewById(R.id.share);
            comment = itemView.findViewById(R.id.comment);
            licked = itemView.findViewById(R.id.licked);
            options = itemView.findViewById(R.id.options);
            location = itemView.findViewById(R.id.location);
            openProfile = itemView.findViewById(R.id.openProfile);
        }
    }
}
