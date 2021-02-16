package com.e.instagram.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.e.instagram.MainActivity;
import com.e.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(welcome.this, login.class));
                finish();
            }
        },3000);



    }


}