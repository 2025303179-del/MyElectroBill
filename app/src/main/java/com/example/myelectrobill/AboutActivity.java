package com.example.myelectrobill;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AboutActivity extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this,
                    CreateCalculateBillActivity.class);
            startActivity(intent);
        });

        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_about);
        nav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                Intent intent = new Intent(AboutActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            if(item.getItemId() == R.id.nav_about){
                return true;
            }
            return false;
        });
        }
    }
