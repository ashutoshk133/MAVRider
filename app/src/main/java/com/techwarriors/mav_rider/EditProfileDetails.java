package com.techwarriors.mav_rider;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
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

public class EditProfileDetails extends AppCompatActivity {

    public String rideruid;
    EditText rname,rphone,rsq,rsa;
    DBCollection ridercollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Bundle extras = getIntent().getExtras();
        rideruid=extras.getString("riderutaid");*/
       Intent i2=getIntent();
       rideruid=i2.getStringExtra("rutaid");

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        rname=(EditText)findViewById(R.id.eteditname);
        rphone=(EditText)findViewById(R.id.eteditphone);
        rsq=(EditText)findViewById(R.id.eteditsecque);
        rsa=(EditText)findViewById(R.id.eteditsecans);

        BasicDBObject bdo = new BasicDBObject("r_utaid", rideruid);
        MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
        ridercollection = db.getCollection(Connectivity.COLLECTION_NAME1);
        DBCursor cursor=ridercollection.find(bdo);
//        Toast.makeText(getApplicationContext(),cursor.count(), Toast.LENGTH_LONG).show();
        while (cursor.hasNext()){
            BasicDBObject dbobj=(BasicDBObject)cursor.next();
            String ridername =  dbobj.get("r_name").toString();
            String riderphone = dbobj.get("r_phone").toString();
            String ridersq =  dbobj.get("r_sec_que").toString();
            String ridersa = dbobj.get("r_sec_ans").toString();

            rname.setText(ridername);
            rphone.setText(riderphone);
            rsq.setText(ridersq);
            rsa.setText(ridersa);

        }

    }

    public void onUpdate(View v)
    {
        try {

            String newname = rname.getText().toString();
            String newphone = rphone.getText().toString();
            String newsecque = rsq.getText().toString();
            String newsecans = rsa.getText().toString();

            MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
            ridercollection = db.getCollection("rider");

            BasicDBObject setname = new BasicDBObject("$set", new BasicDBObject("r_name", newname));
            BasicDBObject setphone = new BasicDBObject("$set", new BasicDBObject("r_phone", newphone));
            BasicDBObject setsecque = new BasicDBObject("$set", new BasicDBObject("r_sec_que", newsecque));
            BasicDBObject setsecans = new BasicDBObject("$set", new BasicDBObject("r_sec_ans", newsecans));
            ridercollection.update(new BasicDBObject("r_utaid",rideruid), setname);
            ridercollection.update(new BasicDBObject("r_utaid",rideruid), setphone);
            ridercollection.update(new BasicDBObject("r_utaid",rideruid), setsecque);
            ridercollection.update(new BasicDBObject("r_utaid",rideruid), setsecans);
            Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
            Intent i3=new Intent(EditProfileDetails.this,RequestRide.class);
            i3.putExtra("utaid",rideruid);
            startActivity(i3);


        } catch (MongoTimeoutException mte) {
            mte.printStackTrace();
        } catch (MongoIncompatibleDriverException mide) {
            mide.printStackTrace();
        } catch (Exception exe) {
            exe.printStackTrace();
        }

    }


}
