package com.e.instagram.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.e.instagram.Adapter.SearchAdapter;
import com.e.instagram.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class search extends Fragment {

    LinearLayout lotty ;
    androidx.appcompat.widget.SearchView searchView ;
    String search_txt;
    RecyclerView recyclerView ;
    SearchAdapter adapter ;
    ImageView back ;
    ArrayList<String> getUsers ,getName, imageUri,UID;
    CollectionReference mRef = FirebaseFirestore.getInstance().collection("Users");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view =  inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchView_1);
        recyclerView = view.findViewById(R.id.list);
        lotty = view.findViewById(R.id.lotty);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new home();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getUsers = new ArrayList<>();
                getName = new ArrayList<>();
                imageUri = new ArrayList<>();

                Query query1 = mRef.orderBy("userName").startAt(query).endAt(query+"\uf8ff");
                query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        getUsers.clear();
                        getName.clear();
                        imageUri.clear();
                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType()== DocumentChange.Type.ADDED){
                                getUsers.add(doc.getDocument().get("userName").toString());
                                getName.add(doc.getDocument().get("Name").toString());
                                imageUri.add(doc.getDocument().get("Profile").toString());
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new SearchAdapter(view.getContext(), getName,getUsers, imageUri, UID);
                            recyclerView.setAdapter(adapter);
                        }


                    }

                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    getUsers = new ArrayList<>();
                    getName = new ArrayList<>();
                    imageUri = new ArrayList<>();
                    UID = new ArrayList<>();
                    lotty.setVisibility(View.GONE);

                    Query query = mRef.orderBy("userName").startAt(newText).endAt(newText+"\uf8ff");
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            getUsers.clear();
                            getName.clear();
                            imageUri.clear();
                            for (DocumentChange doc : value.getDocumentChanges()){
                                if (doc.getType()== DocumentChange.Type.ADDED){
                                    UID.add(doc.getDocument().get("userID").toString());
                                    getUsers.add(doc.getDocument().get("userName").toString());
                                    getName.add(doc.getDocument().get("Name").toString());
                                    imageUri.add(doc.getDocument().get("Profile").toString());

                                }else {
                                    getUsers.clear();
                                    getName.clear();
                                    imageUri.clear();
                                    UID.clear();
                                }
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new SearchAdapter(view.getContext(), getName,getUsers, imageUri,UID);
                            recyclerView.setAdapter(adapter);

                        }

                    });
                }
                else {
                    getUsers.clear();
                    getName.clear();
                    imageUri.clear();
                    UID.clear();
                    lotty.setVisibility(View.VISIBLE);
                }



                return false;
            }
        });
       return view ;
    }



}