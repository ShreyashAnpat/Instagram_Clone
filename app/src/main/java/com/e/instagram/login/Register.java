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

public class Register extends AppCompatActivity {
    EditText email , password  ;
    TextView register , login ;
    String email_txt , password_txt ;
    FirebaseAuth auth ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.log_in);
        auth = FirebaseAuth.getInstance() ;

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_txt = email.getText().toString() ;
                password_txt = password.getText().toString();
                if(email_txt.isEmpty() || password_txt.isEmpty()){
                    Toast.makeText(Register.this, "Email or password is empty", Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email_txt , password_txt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Register.this, "Register successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this , persnol_info.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, e.toString(), Toast.LENGTH_SHORT).show();
                            email.getText().clear();
                            password.getText().clear();
                        }
                    });

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this , login.class));
            }
        });

    }
}