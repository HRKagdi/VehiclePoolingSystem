package com.example.vehiclepooling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import util.Connection;
import util.OnUpdated;

public class CustomAdapter extends BaseAdapter {
  List<Document> data;
  Activity activity;
  Connection con=Connection.getConnection();
  private int screen;// 0 for vehicle 1 for rider 2 for pooler
  private static LayoutInflater inflater=null;
  public CustomAdapter(List<Document> result, Activity activity,int screen){
    data=result;
    this.screen=screen;
    this.activity =activity;
    inflater = ( LayoutInflater )activity.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Object getItem(int position) {
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
  public static class ViewHolder{
    public TextView data;
    Button select;
  }

  /****** Depends upon data size called for each row , Create each ListView row *****/
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder; // view lookup cache stored in tag

    if (convertView == null) {

      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(activity);
      convertView = inflater.inflate(R.layout.custom_adapter, parent, false);
      viewHolder.data = convertView.findViewById(R.id.Data);
      viewHolder.select = convertView.findViewById(R.id.select);

      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();

    }
    StringBuilder toDisplay=new StringBuilder();
    if(screen==2){
      toDisplay.append("UserName :"+data.get(position).get("username").toString()+"\n");
      toDisplay.append("Gender :"+data.get(position).get("gender").toString()+"\n");
      toDisplay.append("Mobile Number :"+data.get(position).get("number").toString()+"\n");
      /*List<Document> data1= (List<Document>) data.get(position).get("vehicle");

      toDisplay.append("Vehicle Name :" + data1.get(position).get("Name").toString() + "\n");
      toDisplay.append("Seater :" + data1.get(position).get("Seater").toString() + "\n");
      toDisplay.append("Color :" + data1.get(position).get("Color").toString() + "\n");
      toDisplay.append("Registered Number :" + data1.get(position).get("Registration_Number").toString());*/
    }
    toDisplay.append("Vehicle Name :" + data.get(position).get("Name").toString() + "\n");
    toDisplay.append("Registered Number :" + data.get(position).get("Registration_Number").toString()+"\n");
    if(screen!=0) {
      toDisplay.append("Available Seat:" + data.get(position).get("AvailableSeat").toString() + "\n");
      toDisplay.append("JourneyTime :" + data.get(position).get("JourneyTime").toString() + "\n");
    }
    if(screen==0)
      toDisplay.append("Seater :" + data.get(position).get("Seater").toString() + "\n");
    toDisplay.append("Color :" + data.get(position).get("Color").toString());
    if(screen == 1){
      toDisplay.append("\nSource :" + data.get(position).get("Source").toString() + "\n");
      toDisplay.append("Destination:" + data.get(position).get("Destination").toString() + "\n");
    }

    viewHolder.data.setText(toDisplay);
    viewHolder.select.setOnClickListener(v -> {
      if(screen == 0) {
        Toast.makeText(activity, "Selected Vehicle" + data.get(position).get("Name").toString(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(activity, SourceDestinationSeatSelection.class);
        i.putExtra("seat", Integer.parseInt(data.get(position).get("Seater").toString().trim()));
        i.putExtra("RegisterNO", data.get(position).get("Registration_Number").toString());
        activity.startActivity(i);
      }else if(screen ==2){
        Document doc=new Document();
        doc.put("Registration_Number",data.get(position).get("Registration_Number"));
        SharedPreferences pref = activity.getSharedPreferences("LoginDetails", 0); // 0 - for private mode
        Document d1=new Document("email",pref.getString("Email_ID",null));

        //Document d3=new Document( "$push",);
        Document updateDoc=new Document("$push",new Document("AvailableVehicle.$.Request",new Document(d1)));
        //Document updateDoc = new Document("$addToSet",d4);

        Document d2 = new Document().append("Registration_Number", data.get(position).get("Registration_Number").toString().trim());
        Document filterDoc=new Document("$elemMatch",d2);
        con.updateData(new Document("AvailableVehicle",filterDoc), updateDoc, "User", isSuccess -> {
          Toast.makeText(activity, "Success = "+isSuccess, Toast.LENGTH_SHORT).show();
        });
      }
    });
    return convertView;
  }
}