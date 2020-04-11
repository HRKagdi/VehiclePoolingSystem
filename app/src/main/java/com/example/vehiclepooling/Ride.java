package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import org.bson.Document;


import java.util.List;

import util.Connection;
import util.Hashing;

public class Ride extends AppCompatActivity {
    Connection con=Connection.getConnection();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        listView=findViewById(R.id.rides);
        Document doc=new Document();
        SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",0);
        doc.put("email",sharedPreferences.getString("Email_ID",null));

        ProgressDialog pd= Hashing.getProgressDialog(this,"Loading vehicles");
        pd.show();
        con.findData(doc, "User", data -> {
            pd.dismiss();
            CustomAdapter cs=new CustomAdapter((List<Document>) data.get(0).get("vehicle"),this,0);//0 to find available car
            listView.setAdapter(cs);
        });
    }
}
