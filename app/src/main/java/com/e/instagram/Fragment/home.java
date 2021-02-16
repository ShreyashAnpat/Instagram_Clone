package com.e.instagram.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.instagram.Adapter.postAdapter;
import com.e.instagram.R;
import com.e.instagram.messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class home extends Fragment {
    ImageView   message ;
    RecyclerView recyclerView ;
    com.e.instagram.Adapter.postAdapter postAdapter ;
    FirebaseFirestore db ;
    FirebaseAuth auth ;
    List<String> userName , image , postImage , imageCaption ,UID , location ;
    SwipeRefreshLayout refreshLayout ;
    CircleImageView YourStory ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        refreshLayout = view.findViewById(R.id.refresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        db = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        imageCaption = new ArrayList<>();
        userName = new ArrayList<>();
        image = new ArrayList<>();
        postImage = new ArrayList<>();
        UID = new ArrayList<>();
        location = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        YourStory = view.findViewById(R.id.Story);

        db.collection("Users").document(auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.get("Profile").toString()).placeholder(R.drawable.profile).into(YourStory);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postImage.clear();
                image.clear();
                imageCaption.clear();
                userName.clear();
                UID.clear();
                location.clear();
                db.collection("Poast").orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType()== DocumentChange.Type.ADDED)
                            postImage.add(doc.getDocument().get("Image").toString())  ;
                            imageCaption.add(doc.getDocument().get("Caption").toString());
                            userName.add(doc.getDocument().get("userName").toString())  ;
                            image.add(doc.getDocument().get("profile").toString());
                            UID.add(doc.getDocument().get("UID").toString());
                            location.add(doc.getDocument().get("location").toString());


                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        postAdapter = new postAdapter(view.getContext(), postImage , imageCaption,userName,image, UID ,location);
                        recyclerView.setAdapter(postAdapter);
                        refreshLayout.setRefreshing(false);
                    }
                }
                );
            }
        });
        db.collection("Poast").orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType()== DocumentChange.Type.ADDED){
                        postImage.add(doc.getDocument().get("Image").toString());
                        imageCaption.add(doc.getDocument().get("Caption").toString());
                        userName.add(doc.getDocument().get("userName").toString())  ;
                        image.add(doc.getDocument().get("profile").toString());
                        UID.add(doc.getDocument().get("UID").toString());
                        location.add(doc.getDocument().get("location").toString());
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        postAdapter = new postAdapter(view.getContext(), postImage , imageCaption,userName,image, UID ,location);
                        recyclerView.setAdapter(postAdapter);
                    }


                }
                }


            }
        );
        progressDialog.dismiss();
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), messages.class);
                intent.putExtra("name" , " shreyash");
                startActivity(intent);
            }
        });
        return view ;
    }

}