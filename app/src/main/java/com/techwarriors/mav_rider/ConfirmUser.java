package com.techwarriors.mav_rider;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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


public class ConfirmUser extends AppCompatActivity {
    EditText etui;
    String utaid;
    private DBCollection ridercollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etui=(EditText)findViewById(R.id.etriderutaid);

    }

    public void onVerify(View v)
    {
        try {
            utaid=etui.getText().toString();
            if(utaid.equals(""))
            {
                Snackbar.make(v,"Field Empty",Snackbar.LENGTH_SHORT).show();
            }
            else
            {
                MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                ridercollection = db.getCollection(Connectivity.COLLECTION_NAME1);
                DBCursor cursor = ridercollection.find(new BasicDBObject("r_utaid", utaid));
                if (cursor.count() > 0) {
                    Snackbar.make(v, "Account Already Exists !", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ConfirmUser.this, RegisterARider.class);
                    intent.putExtra("r_utaid",utaid);
                    mongoClient.close();
                    startActivity(intent);
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
