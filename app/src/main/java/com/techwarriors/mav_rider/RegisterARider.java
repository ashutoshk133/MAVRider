package com.techwarriors.mav_rider;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoIncompatibleDriverException;
import com.mongodb.MongoTimeoutException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterARider extends AppCompatActivity {


    public String ridername,riderphone,ridersecans,ridersecque,pass,encrpass,reenterpass,utaid;
    Button regbut;
    private DBCollection ridercollection;
    EditText etridername,etriderphone,etridersecans,etridersecque,etpass,etreenterpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerarider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etridername=(EditText)findViewById(R.id.etridername);
        etriderphone=(EditText)findViewById(R.id.etriderphone);
        etridersecque=(EditText)findViewById(R.id.etridersecque);
        etridersecans=(EditText)findViewById(R.id.etridersecans);
        etpass=(EditText)findViewById(R.id.etpass);
        etreenterpass=(EditText)findViewById(R.id.etreenterpass);
        Intent i=getIntent();
        utaid = i.getStringExtra("r_utaid");
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    private static final String md5(final String pwd) {
        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(pwd.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void onRegister(View v)
    {
        try {

            ridername=etridername.getText().toString();
            riderphone=etriderphone.getText().toString();
            ridersecque=etridersecque.getText().toString();
            ridersecans=etridersecans.getText().toString().toLowerCase();
            pass=etpass.getText().toString();
            reenterpass=etreenterpass.getText().toString();
            if(!pass.equals(reenterpass))
            {
                Snackbar.make(v,"Passwords Do Not Match",Snackbar.LENGTH_SHORT).show();}
                else{
                    if (ridername.equals("") || riderphone.equals("") || ridersecque.equals("") || ridersecans.equals("") || pass.equals("") || reenterpass.equals(""))
                    {
                        Snackbar.make(v,"Please Enter All Fields",Snackbar.LENGTH_SHORT).show();}
                        else
                            {
                                encrpass = md5(pass);
                                BasicDBObject bdo = new BasicDBObject("r_utaid", utaid).append("r_pass", encrpass).append("r_name", ridername).append("r_phone", riderphone).append("r_sec_que", ridersecque).append("r_sec_ans", ridersecans).append("rider_isRiding", "false");
                                MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                                MongoClient mongoClient = new MongoClient(mongoClientURI);
                                DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                                ridercollection = db.getCollection(Connectivity.COLLECTION_NAME1);
                                ridercollection.insert(bdo);
                                mongoClient.close();
                                Toast.makeText(this, "Registered !\n\nLogin to MavEscort", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(this,Login.class);
                                startActivity(i);
                            }
                }

        } catch (MongoTimeoutException mte) {
            mte.printStackTrace();
        } catch (MongoIncompatibleDriverException mide) {
            mide.printStackTrace();
        } catch (Exception exe) {
            exe.printStackTrace();
        }

    }


}
