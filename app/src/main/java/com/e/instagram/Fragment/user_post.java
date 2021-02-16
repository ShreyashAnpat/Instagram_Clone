package com.e.instagram.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.instagram.Adapter.postAdapter;
import com.e.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class user_post extends Fragment {
    RecyclerView recyclerView;
    postAdapter adapter;
    List<String > postImage , imageCaption,userName,image ,UID ,location;
    FirebaseFirestore db ;
    FirebaseAuth auth ;
    ImageView imageView ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_post, container, false);
        recyclerView = view.findViewById(R.id.userPost);
        imageView = view.findViewById(R.id.imageView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        db = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance() ;
        postImage = new ArrayList<>();
        imageCaption = new ArrayList<>();
        userName = new ArrayList<>();
        image = new ArrayList<>();
        UID = new ArrayList<>();
        location = new ArrayList<>();
        Bundle bundle  = getArguments();
        String positions = bundle.getString("position" );
        String UIDs = bundle.getString("UID");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UIDs == auth.getCurrentUser().getUid()){
                    Fragment fragment = new profileFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                else if (UIDs != auth.getCurrentUser().getUid()){
                   getFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();
                }

            }
        });

        db.collection("Poast").whereEqualTo("UID",bundle.getString("UID")).orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    postImage.add(doc.get("Image").toString())  ;
                    imageCaption.add(doc.get("Caption").toString());
                    userName.add(doc.get("userName").toString())  ;
                    image.add(doc.get("profile").toString());
                    UID.add(doc.get("UID").toString());
                    location.add(doc.get("location").toString());
                            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            recyclerView.getLayoutManager().scrollToPosition(Integer.parseInt(positions));
                            adapter = new postAdapter(view.getContext(), postImage , imageCaption,userName,image ,UID ,location );
                            recyclerView.setAdapter(adapter);
                        }

                }

            }
        );

        bundle.clear();
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = getArguments();
                if (bundle.getString("UID")== auth.getCurrentUser().getUid()){
                    Fragment fragment = new profileFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                else {
                    Fragment fragment = new viewProfile();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }
}