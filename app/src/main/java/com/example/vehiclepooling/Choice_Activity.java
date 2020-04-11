package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Choice_Activity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    Button vr,ride,pool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_);
        try
        {
            this.getSupportActionBar().hide();
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        catch (NullPointerException e){}

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "temp");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        findView();
    }

    public void findView(){
        vr=findViewById(R.id.vr);
        ride=findViewById(R.id.ride);
        pool=findViewById(R.id.pool);
    }

    public void pool(View view){
        Intent i = new Intent(Choice_Activity.this,Ride.class);
        startActivity(i);

    }
    public void ride(View view){
        Intent i =new Intent(Choice_Activity.this,SourceDestinationSeatSelection.class);
        i.putExtra("screen",1);
        startActivity(i);
    }

    public void vr(View view){
        Intent i = new Intent(Choice_Activity.this,Vr.class);
        startActivity(i);

    }

    public void Logout(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("Email_ID");
        editor.commit();
        Intent i=new Intent(this,Login_Activity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();

    if(id == R.id.it1){
        Intent i1 = new Intent(this,MainActivity.class);
        startActivity(i1);
        return false;
    }
    if(id == R.id.it2){
        Uri uri = Uri.parse( "https://docs.google.com/forms/d/e/1FAIpQLSeqYOaXDQ1L_ROJb_KccSxK0sN5F4HnyKOgXvl_bcbwjliafg/viewform?usp=sf_link" );
        startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
    }
        return super.onOptionsItemSelected(item);
    }

    public void ride_history(View view) {
        Intent i =new Intent(Choice_Activity.this, Ride_History.class);
        startActivity(i);
    }
}
