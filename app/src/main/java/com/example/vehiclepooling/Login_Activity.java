package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.bson.Document;

import java.lang.annotation.Target;
import java.util.List;

import util.Connection;
import util.Hashing;
import util.OnRetrival;

public class Login_Activity extends AppCompatActivity {
    Connection con=Connection.getConnection();
    TextInputEditText emailid,password;
    Button reset,login;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_login);
        isLogged();
        findView();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailid.setText("");
                password.setText("");
            }
        });

    }

    private void isLogged(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", 0); // 0 - for private mode
        String emailId=pref.getString("Email_ID",null);
        if(emailId!=null){
            Intent i=new Intent(this,Choice_Activity.class);
            startActivity(i);
            finish();
        }
    }

    public void login(View v){
        ProgressDialog pd=Hashing.getProgressDialog(this,"Loging in...!!");
        pd.show();
        Document doc=new Document();
        doc.put("email",emailid.getText().toString().trim());
        doc.put("password", Hashing.convertToMd5(password.getText().toString().trim()));
        con.findData(doc, "User", data -> {
            pd.dismiss();
            if(data.toArray().length>0){
                SharedPreferences pref = getSharedPreferences("LoginDetails", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Email_ID",doc.getString("email"));
                editor.commit();
                Intent i = new Intent(this, Choice_Activity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(this, "Invalid Details", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void findView()
    {
        emailid=findViewById(R.id.email);
        password=findViewById(R.id.pass);
        reset=findViewById(R.id.reset);
        login=findViewById(R.id.login);


    }

    public void signup(View view) {
        Intent i=new Intent(Login_Activity.this,MainActivity.class);
        startActivity(i);
    }
}
