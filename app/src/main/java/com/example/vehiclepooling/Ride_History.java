package com.example.vehiclepooling;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import util.Connection;
import util.Hashing;

public class Ride_History extends AppCompatActivity {

    Connection con = Connection.getConnection();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("Ahemdabad to surat"); // set the top title


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride__histroy);

        listView=findViewById(R.id.selected_rides);
        Document doc;
        doc = new Document();
        SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",0);
        doc.put("email",sharedPreferences.getString("Email_ID",null));
        ProgressDialog pd= Hashing.getProgressDialog(this,"Loading Journeys");
        pd.show();
        con.findData(doc, "User", data -> {
            pd.dismiss();
            List<Document> d1=(List<Document>)data.get(0).get("vehicle");
            List<Document> d2=(List<Document>)data.get(0).get("AvailableVehicle");
            if(d2.size()<1){
                Toast.makeText(this, "No Rides", Toast.LENGTH_SHORT).show();
                finish();
            }
            Log.e("Stich",d1.toString()+"\n"+d2.toString());
            List<Document> ds= new ArrayList<>();
            for(int i=0;i<d2.size();i++){
                String regno=d2.get(i).get("Registration_Number").toString().trim();
                for(int j=0;j<d1.size();j++){
                    if(regno.equals(d1.get(j).get("Registration_Number").toString().trim())){
                        Document d=new Document();
                        d.put("Name",d1.get(j).get("Name"));
                        d.put("AvailableSeat",d2.get(i).get("AvailableSeat"));
                        d.put("Registration_Number",regno);
                        d.put("Color",d1.get(j).get("Color"));
                        d.put("JourneyDate",d2.get(i).get("JourneyDate"));
                        d.put("JourneyTime",d2.get(i).get("JourneyTime"));
                        d.put("Source",d2.get(i).get("Source"));
                        d.put("Destination",d2.get(i).get("Destination"));
                        ds.add(d);
                    }
                }
            }


            CustomAdapter cs=new CustomAdapter(ds,this,1);
            listView.setAdapter(cs);


        });

    }


}
