package com.e.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.e.instagram.Fragment.home;
import com.e.instagram.Fragment.notification;
import com.e.instagram.Fragment.post;
import com.e.instagram.Fragment.profileFragment;
import com.e.instagram.Fragment.search;
import com.e.instagram.login.login;
import com.e.instagram.login.welcome;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav ;
    Fragment fragment ;
    LottieAnimationView notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.botom_nav);
        notification = findViewById(R.id.notification);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nev_home :
                        fragment = new home() ;
                        break;
                    case R.id.nev_post :
                        fragment = new post() ;
                        break;
                    case R.id.nev_profile :
                        fragment = new profileFragment() ;
                        break;
                    case R.id.nev_search :
                        fragment = new search() ;
                        break;
                    case  R.id.nev_notification:
                        fragment = new notification();
                        break;

                }

                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                }
                            return true ;
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               notification.setVisibility(View.INVISIBLE);
            }
        },3000);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new  home()).commit() ;

    }
}