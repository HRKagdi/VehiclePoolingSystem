package com.example.vehiclepooling;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.Connection;
import util.Hashing;
import util.OnInserted;
import util.OnRetrival;
import util.OnUpdated;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;


public class SourceDestinationSeatSelection extends AppCompatActivity {

    private static final int REQUEST_CODE_SOURCE = 1,REQUEST_CODE_DESTINATION = 2;
    private int screen=0;
    Connection con=Connection.getConnection();

    String regNO;
    public static  final String TAG = "SourceDestinationSeat";
    private DatePickerDialog.OnDateSetListener date_listner;

    Button journeyDate,timePicker;
    Button source,destination,submit;
    Spinner select;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWRpNDU5NyIsImEiOiJjazE3ZGU1OHgxZmFyM2xxZG43ZTJ4OTB1In0.0JWKYUZJZMV1olsJSLScuQ");

        setContentView(R.layout.activity_source_destination_seat_selection);
        findViewById();
        Intent i=getIntent();
        screen=i.getIntExtra("screen",0);
        if(screen == 0) {
            regNO=i.getStringExtra("RegisterNO");
            setAvailableSeats();
        }else{
            select.setVisibility(View.INVISIBLE);
            timePicker.setVisibility(View.INVISIBLE);
        }

        setOnClickEvent();

        timePicker.setOnClickListener(v->{
            Calendar cal= Calendar.getInstance();
            TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker1, int hour, int min) {
                    //set selected time to textview
                timePicker.setText(hour+":"+min);
                }
            },cal.HOUR,cal.MINUTE,false);
            dialog.show();
        });

        journeyDate.setOnClickListener(view -> {
            Calendar cal= Calendar.getInstance();
            int year=cal.get(Calendar.YEAR);
            int month=cal.get(Calendar.MONTH);
            int day=cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog d=new DatePickerDialog(
                    SourceDestinationSeatSelection.this,
                    android.R.style.Theme_Black,
                    date_listner,
                    year,month,day
            );

            d.show();
        });

        date_listner = (datePicker, year, month, day) -> {
            Log.d(TAG,"onDateSet: date:"+year+"/"+month+ "/" +day);
            String date=day+"/"+month+"/"+year;
            journeyDate.setText(date);
        };
    }
    void setAvailableSeats(){
        Intent intent = getIntent();
        int maxSeats= intent.getIntExtra("seat",0);
        if(maxSeats == 0 )
            finish();
        ArrayList<Integer> data = new ArrayList<>();
        for (int i=1;i<maxSeats;i++){
            data.add(i);
        }

        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, data);
       // adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        select.setAdapter(adapter);
    }

    private void setOnClickEvent(){
        source.setOnClickListener(v -> getPlaceName(REQUEST_CODE_SOURCE));
        destination.setOnClickListener(v-> getPlaceName(REQUEST_CODE_DESTINATION));
        submit.setOnClickListener(new View.OnClickListener() {
            String sourceString,destinationString,journeyTime,availableSeat,journeyDate;
            @Override
            public void onClick(View view) {
                sourceString = source.getText().toString();
                destinationString = destination.getText().toString();
                journeyTime = timePicker.getText().toString();
                journeyDate = SourceDestinationSeatSelection.this.journeyDate.getText().toString();

                if(screen==0)
                    availableSeat = select.getSelectedItem().toString();
                if (sourceString.equals("Select Source")) {
                    Toast.makeText(SourceDestinationSeatSelection.this, "Please select Source", Toast.LENGTH_SHORT).show();
                } else if (destinationString.equals("Select Destination")) {
                    Toast.makeText(SourceDestinationSeatSelection.this, "Please select destination ", Toast.LENGTH_SHORT).show();
                } else if (journeyDate.equals("Choose date")) {
                    Toast.makeText(SourceDestinationSeatSelection.this, "Please select date ", Toast.LENGTH_SHORT).show();
                } else if (journeyTime.equals("Choose time") && screen==0) {
                    Toast.makeText(SourceDestinationSeatSelection.this, "Please select time", Toast.LENGTH_SHORT).show();
                } else if (sourceString.equals(destinationString)) {
                    Toast.makeText(SourceDestinationSeatSelection.this, "Source and destination are equal!", Toast.LENGTH_SHORT).show();
                } else {
                    if(screen==0)
                        insert();
                    else{
                        find();
                    }

                }
            }
            private void find(){
                Intent i=new Intent(SourceDestinationSeatSelection.this,AvailableRide.class);

                i.putExtra("Source", sourceString);
                i.putExtra("Destination", destinationString);
                i.putExtra("JourneyDate", journeyDate);
                startActivity(i);
            }
            public void insert(){
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", 0); // 0 - for private mode
                String emailId=pref.getString("Email_ID",null);
                if(emailId==NULL)
                    finish();
                Document data = new Document();

                data.put("Source", sourceString);
                data.put("Destination", destinationString);
                data.put("JourneyDate", journeyDate);
                data.put("Registration_Number",regNO);
                data.put("JourneyTime", journeyTime);
                data.put("AvailableSeat", availableSeat);

                Document filterDoc = new Document().append("email", pref.getString("Email_ID",null));
                Document updateDoc = new Document("$push",new Document("AvailableVehicle",data));

                con.updateData(filterDoc, updateDoc, "User", new OnUpdated() {
                    @Override
                    public void onUpdated(boolean isSuccess) {
                        if(isSuccess) {
                            Toast.makeText(SourceDestinationSeatSelection.this, "Successfully added", Toast.LENGTH_SHORT).show();
                            finish();
                        }else
                            Toast.makeText(SourceDestinationSeatSelection.this, "Try Again :/", Toast.LENGTH_SHORT).show();
                    }
                });
                    //ProgressDialog pd= Hashing.getProgressDialog(getApplicationContext(),"Adding Ride");
                    //pd.show();
                    /*
                    con.insertData(data, "AvailableVehicle", isInserted -> {
                    //pd.dismiss();
                    Toast.makeText(SourceDestinationSeatSelection.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    finish();
                    });*/

            }
            }
        );
    }
    private void findViewById(){
        source=findViewById(R.id.source);
        destination=findViewById(R.id.destination);
        submit=findViewById(R.id.submit);
        select=findViewById(R.id.selectSeats);
        journeyDate=findViewById(R.id.dob);
        timePicker=findViewById(R.id.timePicker);
        linearLayout=findViewById(R.id.ll);
    }

    public void getPlaceName(int Request_Code) {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : "pk.eyJ1IjoiYWRpNDU5NyIsImEiOiJjazE3ZGU1OHgxZmFyM2xxZG43ZTJ4OTB1In0.0JWKYUZJZMV1olsJSLScuQ")
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                .build(SourceDestinationSeatSelection.this);
        startActivityForResult(intent, Request_Code);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SOURCE) {
            source.setText(PlaceAutocomplete.getPlace(data).placeName());
        }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DESTINATION) {
            destination.setText(PlaceAutocomplete.getPlace(data).placeName());
        }
    }
}

