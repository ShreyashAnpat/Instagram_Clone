package com.e.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.e.instagram.Fragment.profileFragment;

public class test extends AppCompatActivity {
    TextView click ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        click = findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileFragment fragment =  new profileFragment();
//                fragment.getTargetFragment();
              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
    }
}