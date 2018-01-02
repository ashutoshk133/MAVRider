package com.techwarriors.mav_rider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class Login extends AppCompatActivity {

    private DBCollection ridercollection;
    String utaid,pwd;
    EditText etui;
    EditText etpw;
    SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etui=(EditText)findViewById(R.id.utaid);
        etpw=(EditText)findViewById(R.id.pwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedpref=getSharedPreferences("data",MODE_PRIVATE);
        int number=sharedpref.getInt("isLogged",0);


        if(number==1){
            Intent i=new Intent(this,RequestRide.class);
            i.putExtra("utaid",sharedpref.getString("rid",null));
            startActivity(i);
            finish();
        }

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



    public void onClick(View v)
    {
        try {
            MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
            utaid=etui.getText().toString();
            pwd=etpw.getText().toString();
            ridercollection = db.getCollection(Connectivity.COLLECTION_NAME1);
            String encrpwd=md5(pwd);
            DBCursor cursor = ridercollection.find(new BasicDBObject("r_utaid", utaid)
                    .append("r_pass", encrpwd));
            if (cursor.count() == 0) {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            } else {
                if (cursor.count() > 0)
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString("rid", utaid);
                editor.putInt("isLogged", 1);
                editor.commit();
                Intent intent = new Intent(Login.this, RequestRide.class);
                intent.putExtra("utaid",utaid);
                startActivity(intent);
            }
        } catch (MongoTimeoutException mte) {
            mte.printStackTrace();
        } catch (MongoIncompatibleDriverException mide) {
            mide.printStackTrace();
        } catch (Exception exe) {
            exe.printStackTrace();
        }

    }


    public void onRegister(View v)
    {
                Intent intent = new Intent(Login.this, ConfirmUser.class);
                intent.putExtra("riderutaid",utaid);
                startActivity(intent);
    }

    public void onForget(View v)
    {
        Intent intent = new Intent(Login.this, ForgotPwd.class);
        intent.putExtra("riderutaid",utaid);
        startActivity(intent);
    }


}
