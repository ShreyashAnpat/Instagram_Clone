package com.e.instagram.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class profileFragment extends Fragment {
    private static final int RESULT_OK = -1;
    TextView username , name ,bio   ,editProfile ,postCount ,login , createaccount, following,followers_count;
    FirebaseFirestore db ;
    FirebaseAuth auth ;
    String username_txt , name_txt , bio_txt  , profileUri , count;
    CircleImageView profile ;
    Uri imageUri ;
    StorageReference Imagename,Folder ;
    RecyclerView posts ;
    com.e.instagram.Adapter.myPosts myPosts ;
    List<String>  images ,follower_count ;
    String following_name;
    ImageView addAccount ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        username = view.findViewById(R.id.user_name);
        name = view.findViewById(R.id.name);
        bio = view.findViewById(R.id.bio);
        auth = FirebaseAuth.getInstance();
        db  = FirebaseFirestore.getInstance() ;
        following = view.findViewById(R.id.following_count);
        profile = view.findViewById(R.id.profile_image);
        editProfile = view.findViewById(R.id.edit_profile);
        posts = view.findViewById(R.id.posts);
        postCount = view.findViewById(R.id.posts_count);
        addAccount = view.findViewById(R.id.addAccount);
        followers_count = view.findViewById(R.id.followers_count);

        List<String> following_count ;

        db.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username_txt = documentSnapshot.get("userName").toString();
                name_txt =  documentSnapshot.get("Name").toString();
                bio_txt = documentSnapshot.get("bio").toString();
                profileUri = documentSnapshot.get("Profile").toString();
                username.setText(username_txt);
                name.setText(name_txt);
                bio.setText(bio_txt);
                Picasso.get().load(profileUri).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(profile) ;
            }

        });
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Following")
         .addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                                String count = String.valueOf(value.size());
                                following.setText(count);
                        }
                   }
         });

                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Followers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        String count = String.valueOf(value.size());
                        followers_count.setText(count);
                    }
                });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =new Edite_profile();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        images = new ArrayList<>();
        db.collection("Poast").whereEqualTo("UID" , auth.getCurrentUser().getUid()).orderBy("time" , Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()){
                    for (QueryDocumentSnapshot doc :task.getResult()){
                        images.add(doc.get("Image").toString());
                    }

                    posts.setLayoutManager(new GridLayoutManager(view.getContext(),3));
                    myPosts = new myPosts(view.getContext() , images , auth.getCurrentUser().getUid());
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

        return  view ;
    }

    private void selectimage() {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri= result.getUri();
                profile.setImageURI(imageUri);
                uploadImage( imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        Folder = FirebaseStorage.getInstance().getReference().child("UserProfile");
        Imagename = Folder.child(auth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
        Imagename.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String , Object> update = new HashMap<>();
                        update.put("Profile",uri.toString());
                        DocumentReference image = FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
                        image.update(update) ;
                        FirebaseFirestore PostProfile = FirebaseFirestore.getInstance() ;
                        HashMap<String , Object> uid = new HashMap<>();
                        update.put("UID",uri.toString());
                        PostProfile.collection("Poast").whereEqualTo("UID", auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                                    String id = doc.getId();
                                    FirebaseFirestore.getInstance().collection("Poast").document(id).update(uid) ;
                                    FirebaseFirestore.getInstance().collection("Poast").document(id).collection("Comment").whereEqualTo("UID" , auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot documentReference: queryDocumentSnapshots.getDocuments()){
                                                String id1 = documentReference.getId();
                                                db.collection("Poast").document(id).collection("Comment").document(id1).update(uid);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
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
