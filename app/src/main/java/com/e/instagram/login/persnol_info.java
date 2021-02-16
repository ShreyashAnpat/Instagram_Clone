
package com.e.instagram.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.instagram.MainActivity;
import com.e.instagram.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class persnol_info extends AppCompatActivity {
    FirebaseAuth auth ;
    EditText userName , Name , bio ;
    String username_txt , name_txt, bio_txt ;
    TextView next ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persnol_info);
        userName = findViewById(R.id.username);
        Name = findViewById(R.id.name);
        bio = findViewById(R.id.bio);
        next = findViewById(R.id.next);
        auth = FirebaseAuth.getInstance() ;

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_txt = userName.getText().toString();
                name_txt = Name.getText().toString() ;
                bio_txt = bio.getText().toString();


                if(username_txt.isEmpty() || name_txt.isEmpty()||bio_txt.isEmpty()){
                    Toast.makeText(persnol_info.this, "something is empty", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String , Object > info = new HashMap<>();
                    info.put("userName", username_txt);
                    info.put("Name" , name_txt);
                    info.put("bio",bio_txt);
                    info.put("userID",auth.getCurrentUser().getUid());
                    info.put("Profile" ,"https://firebasestorage.googleapis.com/v0/b/instagram-f5013.appspot.com/o/profile.jpg?alt=media&token=49148b85-5c2a-45d8-8723-e0e99fc52b18");

                    db.collection("Users").document(auth.getCurrentUser().getUid().toString()).set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(persnol_info.this , MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(persnol_info.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        
    }
}