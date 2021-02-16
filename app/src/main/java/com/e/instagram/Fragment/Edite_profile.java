package com.e.instagram.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Edite_profile extends Fragment {
    private static final int RESULT_OK = -1;
    EditText username , name , bio ;
    TextView changeProfile ;
    CircleImageView imageView ;
    ImageView  update ;
    FirebaseFirestore firebaseFirestore ;
    FirebaseAuth auth ;
    Uri imageUri ;
    StorageReference Imagename,Folder ;
    ProgressDialog pd ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edite_profile, container, false);

        username = view.findViewById(R.id.Username);
        name = view.findViewById(R.id.name);
        bio = view.findViewById(R.id.bio);
        changeProfile = view.findViewById(R.id.changeProfile);
        imageView = view.findViewById(R.id.image);
        update = (ImageView) view.findViewById(R.id.update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance() ;
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.get("userName").toString());
                name.setText(documentSnapshot.get("Name").toString());
                bio.setText(documentSnapshot.get("bio").toString());
                Picasso.get().load(documentSnapshot.get("Profile").toString()).into(imageView);
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        return view ;
    }

    private void update(){
        HashMap<String , Object> update = new HashMap<>();
//        Toast.makeText(getContext(), username.getText().toString(), Toast.LENGTH_SHORT).show();
        update.put("Name", name.getText().toString());
        update.put("bio" , bio.getText().toString());
        update.put("userName",username.getText().toString());
        DocumentReference image = FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
        image.update(update);
        FirebaseFirestore postss  = FirebaseFirestore.getInstance() ;
        postss.collection("Poast").whereEqualTo("UID", auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               for (DocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                   HashMap<String , Object> postInfo = new HashMap<>();
                   postInfo.put("userName",username.getText().toString() );
                    postss.collection("Poast").document(queryDocumentSnapshot.getId()).update(postInfo);
               }
            }
        });
        Fragment fragment = new profileFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
    private void selectimage() {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading");
        pd.show();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri= result.getUri();
                imageView.setImageURI(imageUri);

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
                        FirebaseFirestore postss = FirebaseFirestore.getInstance() ;
                        postss.collection("Poast").whereEqualTo("UID", auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                                    HashMap<String , Object> postInfo = new HashMap<>();
                                    postInfo.put("profile", uri.toString());
                                    postss.collection("Poast").document(queryDocumentSnapshot.getId()).update(postInfo);
                                }
                            }
                        });
                        DocumentReference image = FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
                        image.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Fragment fragment = new profileFragment();
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                pd.dismiss();
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
                Fragment fragment = new profileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

}
