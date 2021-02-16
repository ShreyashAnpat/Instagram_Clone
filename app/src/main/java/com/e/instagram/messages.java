package com.e.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.instagram.Adapter.SearchAdapter;
import com.e.instagram.Adapter.chat_user;
import com.e.instagram.Fragment.home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class messages extends AppCompatActivity {

    TextView Username ;
    ImageView back ;
    RecyclerView recyclerView ;
    FirebaseAuth auth ;
    FirebaseFirestore  db ;
    chat_user chat_user ;
    androidx.appcompat.widget.SearchView searchView ;
    CollectionReference mRef = FirebaseFirestore.getInstance().collection("Users");
    List<String> username , profile , name;
    LinearLayout lotty ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Username = findViewById(R.id.Username);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.Contacts);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        searchView = findViewById(R.id.searchView);
        lotty = findViewById(R.id.lotty);
        Intent intent = getIntent();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   messages.super.onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    recyclerView.setVisibility(View.VISIBLE);
                    lotty.setVisibility(View.INVISIBLE);
                    username = new ArrayList<>();
                    name = new ArrayList<>();
                    profile = new ArrayList<>();
                    Query query1 = mRef.document(auth.getCurrentUser().getUid()).collection("Following").orderBy("user_name").startAt(newText).endAt(newText+"\uf8ff");
                    query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            username.clear();
                            profile.clear();
                            for (DocumentChange doc : value.getDocumentChanges()){
                                if (doc.getType()== DocumentChange.Type.ADDED){
                                    username.add(doc.getDocument().get("user_name").toString());
                                    profile.add(doc.getDocument().get("profile").toString());
                                }else {
                                    username.clear();
                                    profile.clear();
                                }
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(messages.this));
                            chat_user = new chat_user(messages.this , username,profile);
                            recyclerView.setAdapter(chat_user);

                        }
                    });
                }
                else {
                username.clear();
                profile.clear();
                recyclerView.setVisibility(View.INVISIBLE);
                lotty.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


    }
}