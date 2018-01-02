package com.techwarriors.mav_rider;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResetPass extends AppCompatActivity {

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection ridercollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Intent intent=getIntent();
        final String utaid=intent.getStringExtra("rid");

        final EditText etpass=(EditText)findViewById(R.id.newpwd);
        final EditText etcpass=(EditText)findViewById(R.id.confirmpwd);

        Button reset=(Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newpass=etpass.getText().toString();
                String confirm=etcpass.getText().toString();
                if (!newpass.equals("")&&!confirm.equals("")&&newpass.equals(confirm))
                {
                    String encryptedPwd=md5(newpass);

                    try{
                        mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                        mongoClient = new MongoClient(mongoClientURI);
                        DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                        ridercollection=db.getCollection("rider");
                        DBCursor cursor=ridercollection.find(new BasicDBObject("r_utaid",utaid));
                        if(cursor.count()>0)
                        {
                            BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("r_pass", encryptedPwd));
                            ridercollection.update(new BasicDBObject("r_utaid",utaid), set);
                            Toast.makeText(getApplicationContext(),"Password changed Sucessful\nPlease Login",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(ResetPass.this,Login.class);
                            startActivity(i);
                            finish();
                        }


                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private static final String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
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

}
