package com.example.vehiclepooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import util.Connection;
import util.Hashing;
import util.OnUpdated;


public class Vr extends AppCompatActivity {
    private Spinner spinner;
    Connection con=Connection.getConnection();


    TextInputEditText vh_type,vh_name,vh_reg,vh_color;
    Button register,reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        findView();
        ArrayAdapter<String> ad=new ArrayAdapter<String>(Vr.this,
                android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.vh_type));
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }

    public void findView(){
        spinner=findViewById(R.id.spinner);
        vh_name=findViewById(R.id.et_vh_name);
        vh_reg=findViewById(R.id.et_vh_number);
        vh_color=findViewById(R.id.et_vh_color);
        reset=findViewById(R.id.reset);
        register=findViewById(R.id.bt1);
    }
    public void register(View v) {
        String ty = spinner.getSelectedItem().toString();
        String na = vh_name.getText().toString().trim();
        String re = vh_reg.getText().toString().trim();
        String co = vh_color.getText().toString().trim();
        if (na.isEmpty()) {
            vh_name.setError("Enter name of your Vehicle");
        } else if (re.isEmpty()) {
            vh_reg.setError("Enter Vehicle Passing Number");
        } else if ( re.length() <10) {

            vh_reg.setError("Enter valid passing Number");
        } else if (co.isEmpty()) {

            vh_color.setError("Enter Vehicle Color");
        } else {
            ArrayList<Document> data=new ArrayList<>();
            SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", 0); // 0 - for private mode
            //SharedPreferences.Editor editor = pref.edit();
                Document d=new Document();
                d.put("Seater",ty);
                d.put("Name",na);
                d.put("Registration_Number",re.toUpperCase());
                d.put("Color",co);
            data.add(d);
            ProgressDialog pd= Hashing.getProgressDialog(this,"Registring Vehicle");
            pd.show();
            Document filterDoc = new Document().append("email", pref.getString("Email_ID",null));
            Document updateDoc = new Document("$push",new Document("vehicle",d));

            con.updateData(filterDoc,updateDoc,"User", isSuccess -> {
                pd.dismiss();
                if(isSuccess)
                    Toast.makeText(this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Registration UnSuccessfull", Toast.LENGTH_SHORT).show();
            });
            /*con.insertData(data, "User", isInserted -> {
                pd.dismiss();
                if(isInserted)
                    Toast.makeText(this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Registration UnSuccessfull", Toast.LENGTH_SHORT).show();
            });*/
        }
    }

    public void reset(View view) {

        vh_type.setText("");
        vh_name.setText("");
        vh_reg.setText("");
        vh_color.setText("");
        vh_type.setFocusable(true);

    }
}
