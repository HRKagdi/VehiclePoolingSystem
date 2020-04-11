package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_splash_screen);
        iv=findViewById(R.id.iv);
        iv.setImageResource(R.drawable.splashscreen);
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(SplashScreen.this,Login_Activity.class);
            startActivity(mainIntent);
            finish();
        }, 2500);
        //setContentView(R.layout.activity_splash_screen);
    }
}
