package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.mongodb.DBObject;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import util.Connection;
import util.Hashing;
import util.OnRetrival;

public class AvailableRide extends AppCompatActivity {
    Connection con=Connection.getConnection();
    ListView ll;
    List<Document> data,data1,data2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_ride);
        ll=findViewById(R.id.available);

        ProgressDialog pd= Hashing.getProgressDialog(this,"Finding Rides");
        pd.show();

        String Source=getIntent().getStringExtra("Source");
        String destination=getIntent().getStringExtra("Destination");
        String journeyDate=getIntent().getStringExtra("JourneyDate");


        Document data=new Document();
        data.append("Source", Source);
        data.append("Destination", destination);
        data.append("JourneyDate", journeyDate);

        Document d=new Document();//match
        d.put("AvailableVehicle",new Document("$elemMatch",data));

        con.findData(d,"User",data1 -> {
            pd.dismiss();
            Log.e("Stich",data1.toString());
            if(data1.size()==0) {
                Toast.makeText(this, "No Rides Found", Toast.LENGTH_SHORT).show();
                finish();
            }

            List<Document> ds=new ArrayList<>();
            for(int i=0;i<data1.size();i++){
                List<Document> data2=(List<Document>) data1.get(i).get("AvailableVehicle");
                for(int j=0;j<data2.size();j++){
                    if(Source.equals(data2.get(j).get("Source")) && destination.equals(data2.get(j).get("Destination")) && journeyDate.equals(data2.get(j).get("JourneyDate"))){
                        List<Document> d2=(List<Document>)data1.get(i).get("vehicle");
                        int t=-1;
                        for(int k=0;k<d2.size();k++){
                            if(d2.get(k).get("Registration_Number").equals(data2.get(j).get("Registration_Number"))){
                                t=k;
                            }
                        }
                        if(t!=-1) {
                            Document d1=new Document();
                            d1.put("username",data1.get(i).get("username"));
                            d1.put("gender",data1.get(i).get("gender"));
                            d1.put("number",data1.get(i).get("number"));
                            d1.put("email",data1.get(i).get("email"));

                            d1.put("Name", d2.get(t).get("Name"));
                            d1.put("Registration_Number", data2.get(j).get("Registration_Number"));
                            d1.put("Color", d2.get(t).get("Color"));
                            d1.put("AvailableSeat", data2.get(j).get("AvailableSeat"));
                            d1.put("JourneyDate", data2.get(j).get("JourneyDate"));
                            d1.put("JourneyTime", data2.get(j).get("JourneyTime"));
                            ds.add(d1);
                        }
                        Log.e("StichData",t+":"+d2.toString());
                    }
                }
            }
            CustomAdapter cs=new CustomAdapter(ds,this,2);
            ll.setAdapter(cs);
        });
        /*
        Document data1=new Document();
        data1.append("$$vehicle.Source", Source);
        data1.append("$$vehicle.Destination", destination);
        data1.append("$$vehicle.JourneyDate", journeyDate);
        Document d1=new Document();
        d1.put("input","$AvailableVehicle");
        d1.put("as","vehicle");

        Document d2=new Document();
        d2.put("$eq",data);

        d1.put("cond",d2);
        Document d3=new Document();
        d3.put("$filter",d1);

        Document d4=new Document("AvailableVehicle",d3);
        Document d5=new Document("$project",d4);
        List<Document> ds=new ArrayList<>();
        ds.add(d);
        ds.add(d5);

        con.aggregateFun(ds,"User");
*/
    }
}
