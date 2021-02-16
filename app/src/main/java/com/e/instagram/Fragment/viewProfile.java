package com.e.instagram.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.instagram.Adapter.myPosts;
import com.e.instagram.Models.follow;
import com.e.instagram.R;
import com.e.instagram.login.Register;
import com.e.instagram.login.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
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

public class viewProfile extends Fragment {
    private static final int RESULT_OK = -1;
    TextView username , name ,bio   ,follow ,followings,postCount ,login , createaccount,following_count,followers_count;
    FirebaseFirestore db ;
    LinearLayout following ;
    FirebaseAuth auth ;
    String username_txt , name_txt , bio_txt  , profileUri , count;
    CircleImageView profile ;
    RecyclerView posts ;
    com.e.instagram.Adapter.myPosts myPosts ;
    List<String> images;
   public List<String> shre = new ArrayList<>();

    ImageView addAccount ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_view_profile, container, false);
        username = view.findViewById(R.id.user_name);
        name = view.findViewById(R.id.name);
        bio = view.findViewById(R.id.bio);
        auth = FirebaseAuth.getInstance();
        db  = FirebaseFirestore.getInstance() ;
        profile = view.findViewById(R.id.profile_image);
        posts = view.findViewById(R.id.posts);
        postCount = view.findViewById(R.id.posts_count);
        addAccount = view.findViewById(R.id.addAccount);
        follow = view.findViewById(R.id.follow);
        following = view.findViewById(R.id.following_layout);
        followings = view.findViewById(R.id.following);
        following_count = view.findViewById(R.id.following_count);
        followers_count = view.findViewById(R.id.followers_count);
        Bundle bundle = getArguments();
        String user = bundle.getString("Users");
        String User_profile = bundle.getString("Profile");
        String user_Name = bundle.getString("Username");
        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setMessage("Sending Request");
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(false);
        follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    follow.setVisibility(View.INVISIBLE);
                    following.setVisibility(View.VISIBLE);
                    com.e.instagram.Models.follow putFollow = new follow(user,user_Name,User_profile);

                    db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Following").add(putFollow).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            pd.cancel();
                        }
                    });

                    db.collection("Users").document(user).collection("Followers").add(putFollow).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            pd.cancel();
                        }
                    });

                }
        });
        db.collection("Users").document(user).collection("Following")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        String name = String.valueOf(value.size());
                        following_count.setText(name);
                    }
                });
        db.collection("Users").document(user).collection("Followers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        String count = String.valueOf(value.size());
                        followers_count.setText(count);
                    }
                });

        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Following").whereEqualTo("userID",user ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()){
                   for (DocumentChange doc : task.getResult().getDocumentChanges()){
                       if (doc.getDocument().get("userID").toString().equals(user)){
                           follow.setVisibility(View.INVISIBLE);
                           following.setVisibility(View.VISIBLE);
                       }
                   }
                }
            }
        });


            followings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.INVISIBLE);
                }
            });
            db.collection("Users").document(bundle.getString("Users")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    username_txt = documentSnapshot.get("userName").toString();
                    name_txt = documentSnapshot.get("Name").toString();
                    bio_txt = documentSnapshot.get("bio").toString();
                    profileUri = documentSnapshot.get("Profile").toString();
                    username.setText(username_txt);
                    name.setText(name_txt);
                    bio.setText(bio_txt);
                    Picasso.get().load(profileUri).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(profile);
                }

            });


            images = new ArrayList<>();
            db.collection("Poast").whereEqualTo("UID", bundle.getString("Users")).orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isComplete()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            images.add(doc.get("Image").toString());
                        }
                        posts.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                        myPosts = new myPosts(view.getContext(), images, bundle.getString("Users"));
                        posts.setAdapter(myPosts);
                        count = String.valueOf(images.size());
                        postCount.setText(count);
                    }

                }
            });

            addAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.bottom_sheet);
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                    login = dialog.findViewById(R.id.login);
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            auth.signOut();
                            startActivity(new Intent(profile.getContext(), login.class));

                        }
                    });
                    createaccount = dialog.findViewById(R.id.create_account);
                    createaccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(profile.getContext(), Register.class));
                        }
                    });

                }
            });
            return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = new home();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

}