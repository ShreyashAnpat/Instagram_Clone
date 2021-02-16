package com.e.instagram.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.textclassifier.ConversationAction;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.instagram.Adapter.comments;
import com.e.instagram.MainActivity;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.airbnb.lottie.L.TAG;

public class Comment_Fragment extends Fragment {
    EditText comment ;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore ;
    CircleImageView profile;
    TextView send ;
    RecyclerView comments ;
    ImageView back ;
    FirebaseFirestore db ;
    com.e.instagram.Adapter.comments Adapter ;
    String id ;
    List<String> Comment , dp,username ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_, container, false);
        comment = view.findViewById(R.id.comment);
        comments = view.findViewById(R.id.comments);
        send = view.findViewById(R.id.sendComment);
        back = view.findViewById(R.id.back);
        profile = view.findViewById(R.id.circleImageView);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance() ;
        Comment = new ArrayList<>();
        dp = new ArrayList<>();
        username = new ArrayList<>();
        Bundle bundle = getArguments();
        String PostUri = bundle.getString("PostImage");
        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        db.collection("Poast").whereEqualTo("Image",PostUri).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                id = queryDocumentSnapshots.getDocuments().get(0).getId();

                db.collection("Poast").document(id).collection("Comment").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent( QuerySnapshot value,  FirebaseFirestoreException error) {
                            for (DocumentChange ds : value.getDocumentChanges()){
                                if (ds.getType()== DocumentChange.Type.ADDED){
                                    Comment.add(ds.getDocument().get("Comment").toString());
                                    username.add(ds.getDocument().get("Username").toString());
                                    dp.add(ds.getDocument().get("userProfile").toString());
                                }
                            }
                        comments.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        Adapter = new comments(view.getContext(), Comment,username,dp);
                        comments.getLayoutManager().scrollToPosition(username.size()-1);
                        comments.setAdapter(Adapter);
                    }
                });
                progressDialog.dismiss();
            }
        });



        Log.d(TAG, "onCreateView: " + id);
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String uri = documentSnapshot.get("Profile").toString();
                Picasso.get().load(uri).placeholder(R.drawable.profile).into(profile);
                comment.setHint("Comment as " + documentSnapshot.get("userName").toString() +"...");
            }
        });

        ProgressDialog progressDialog1 = new ProgressDialog(view.getContext());
        progressDialog1.setCanceledOnTouchOutside(false);
        progressDialog1.setMessage("Loading Comment");
        progressDialog.show();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getArguments();
                firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uri = documentSnapshot.get("Profile").toString();
                        HashMap<String , Object> uploadComment = new HashMap<>();
                        uploadComment.put("Username", bundle.getString("Username"));
                        uploadComment.put("userProfile",uri );
                        uploadComment.put("Comment",comment.getText().toString());
                        uploadComment.put("time" , getCurrentTimeStamp());
                        uploadComment.put("UID", auth.getCurrentUser().getUid());
                        db.collection("Poast").whereEqualTo("Image",bundle.getString("PostImage")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isComplete()){
                                    for (DocumentSnapshot doc : task.getResult()){
                                        db.collection("Poast").document(doc.getId()).collection("Comment").document().set(uploadComment);
                                    }
                                }
                            }
                        });
                        progressDialog1.dismiss();
                        comment.setText("");
                    }
                });
            }

            private Object getCurrentTimeStamp() {
                    Long timestamp = System.currentTimeMillis()/1000;
                    return timestamp ;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new home();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit() ;
            }
        });

        return  view ;

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
