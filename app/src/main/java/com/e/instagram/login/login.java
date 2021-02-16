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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText email , password  ;
    TextView createAccount, login ;
    String email_txt , password_txt ;
    FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email );
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.create_account);
        auth = FirebaseAuth.getInstance() ;

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this , Register.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_txt = email.getText().toString() ;
                password_txt = password.getText().toString();
                if(email_txt.isEmpty() || password_txt.isEmpty()){
                    Toast.makeText(login.this, "Email or Password is empty", Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(email_txt, password_txt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(login.this , MainActivity.class));
            finish();
        }
    }
}