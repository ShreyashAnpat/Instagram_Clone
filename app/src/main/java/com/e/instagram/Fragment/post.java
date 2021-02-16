package com.e.instagram.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.instagram.MainActivity;
import com.e.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class post extends Fragment {
    private static final int RESULT_OK = -1;
    ImageView imageView ,post  ;
    EditText caption , location ;
    Uri imageUri ;
    StorageReference Imagename,Folder ;
    FirebaseAuth auth ;
    FirebaseFirestore db ;
    String Username , profileUri;
    Date time ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_poast, container, false);
        imageView = view.findViewById(R.id.image);
        caption = view.findViewById(R.id.caption);
        post = view.findViewById(R.id.post);
        location = view.findViewById(R.id.location);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance() ;
        time = Calendar.getInstance().getTime();
        selectImage();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Uploading Post");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                db.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profileUri = documentSnapshot.get("Profile").toString();
                        Username = documentSnapshot.get("userName").toString();
                    }
                });
                Folder = FirebaseStorage.getInstance().getReference().child("UserProfile");
                Imagename = Folder.child(auth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
                Imagename.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String , Object> upload = new HashMap<>();
                                upload.put("Image",uri.toString());
                                upload.put("Caption",caption.getText().toString());
                                upload.put("UID" , auth.getCurrentUser().getUid().toString());
                                upload.put("userName", Username);
                                upload.put("profile",profileUri);
                                upload.put("location",location.getText().toString());
                                upload.put("time",getCurrentTimeStamp());
                                db.collection("Poast").document().set(upload);
                                startActivity(new Intent(view.getContext(), MainActivity.class));

                                progressDialog.cancel();
                                Fragment fragment = new home() ;
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                            }

                            private Long getCurrentTimeStamp() {
                                Long timestamp = System.currentTimeMillis()/1000;
                                return timestamp ;
                            }
                        });
                    }
                });
            }
        });
        return view ;
    }

    private void selectImage() {
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
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}