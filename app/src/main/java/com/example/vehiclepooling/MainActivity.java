package com.example.vehiclepooling;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.security.MessageDigest;
import java.util.Calendar;

import util.Connection;
import util.Hashing;
import util.OnInserted;

public class MainActivity extends AppCompatActivity {

    private TextView datetv;
    public static  final String TAG = "MainActivity";
    private DatePickerDialog.OnDateSetListener date_listner;

    EditText user,pass,phone,dob,add,cpass,email;
    RadioGroup rg;
    Button signup,date;

    Connection con=Connection.getConnection();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            this.getSupportActionBar().hide();
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        catch (NullPointerException e){}


        findView();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d=new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Black,
                        date_listner,
                        year,month,day
                );

                d.show();
            }
        });

        date_listner = (datePicker, year, month, day) -> {
            Log.d(TAG,"onDateSet: date:"+year+"/"+month+ "/" +day);
            String da=day+"/"+month+"/"+year;
            date.setText(da);
        };

    }
    public void pickDate(View view){

    }

    public void signup(View view) {

        String Us=user.getText().toString().trim();
        String Pa=pass.getText().toString().trim();
        String Ph=phone.getText().toString().trim();
        String Do=dob.getText().toString().trim();
        String Ad=add.getText().toString().trim();
        String Cp=cpass.getText().toString().trim();
        String Em=email.getText().toString().trim();

        int Rg=rg.getCheckedRadioButtonId();

        if(Us.isEmpty()){
            user.setError("Enter User Name");
        }else if(Pa.isEmpty()){
            pass.setError("Enter Password");
        }else if(Ph.isEmpty()){
            phone.setError("Enter Phone Number");
        }else if(Ph.length()!=10){
            phone.setError("Enter 10 digits of number");
        }else if(Do.isEmpty()){
            dob.setError("Enter Date of Birth");
        }else if(Ad.isEmpty()){
            add.setError("Enter address");
        }else if(Cp.isEmpty()){
            cpass.setError("Enter Confirm Password ");
        }else if(Em.isEmpty()){
            email.setError("Enter email ID");
        }else if(!Em.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
            email.setError("Invalid Email ID");
        }else if(!(Pa.equals(Cp))){
            cpass.setError("Password doesn't match");
        }else{
            int selectedId = rg.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            String gender= ((RadioButton) findViewById(selectedId)).getText().toString().trim();
            Document data=new Document();

            data.put("username",Us);
            data.put("email",Em);
            data.put("password", Hashing.convertToMd5(Pa));
            data.put("address",Ad);
            data.put("number",Ph);
            data.put("gender",gender);
            data.put("dob",Do);

            ProgressDialog pd=Hashing.getProgressDialog(this,"Signing UP");
            pd.show();
            con.insertData(data, "User", isInserted -> {
                pd.dismiss();
                Intent i=new Intent(this,Login_Activity.class);
                startActivity(i);
                finish();
            });
        }
    }

    public void reset(View v){
        user.setText("");
        phone.setText("");
        add.setText("");
        pass.setText("");
        cpass.setText("");
        dob.setText("");
        email.setText("");
        rg.clearCheck();
    }
    public void findView(){
        user=findViewById(R.id.et_user);
        phone=findViewById(R.id.et_phone);
        add=findViewById(R.id.et_add);
        cpass=findViewById(R.id.et_cpass);
        date=findViewById(R.id.date);
        email=findViewById(R.id.et_email);
        pass=findViewById(R.id.et_pass);
        rg=findViewById(R.id.rg1);
        signup=findViewById(R.id.bt1);
       // datetv=findViewById(R.id.t_dob);
    }


}

