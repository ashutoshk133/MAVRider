package com.techwarriors.mav_rider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Timer;
import java.util.TimerTask;

public class CurrentRide extends AppCompatActivity {

    String tripid,tripstatus;
    String riderutaid,src,dest,noriders,dutaid,drivername;
    DBCollection tripcollection,drivercollection,requestcollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv7,tv8,tv9,tv10;
        tv7=(TextView)findViewById(R.id.textView7) ;
        tv8=(TextView)findViewById(R.id.textView8) ;
        tv9=(TextView)findViewById(R.id.textView9) ;
        tv10=(TextView)findViewById(R.id.textView10);

        Intent i=getIntent();
        riderutaid=i.getStringExtra("riderid");
        src=i.getStringExtra("src");
        dest=i.getStringExtra("dest");
        noriders=i.getStringExtra("noofriders");
        dutaid=i.getStringExtra("dutaid");




        tv7.setText(src);
        tv8.setText(dest);
        tv9.setText(noriders);

        MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
        tripcollection = db.getCollection("trip");
        DBCursor cur = tripcollection.find(new BasicDBObject("r_utaid",riderutaid));
        final double initcnt=cur.count();
        drivercollection = db.getCollection("driver");
        DBCursor drivernamecur = drivercollection.find(new BasicDBObject("d_utaid",dutaid));
        BasicDBObject dno = (BasicDBObject) drivernamecur.next();
        String drvname=dno.get("d_name").toString();
        tv10.setText(drvname);
        mongoClient.close();
       // NotifyNow();
        final Timer timer=new Timer();
        long delay=10;
        long intervalPeriod = 2000;
        TimerTask timerTask=new TimerTask() {
            @Override

            public void run() {


                try{
                    MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                    MongoClient mongoClient = new MongoClient(mongoClientURI);
                    DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                    tripcollection = db.getCollection("trip");

                    double chkcnt=0;

                    DBCursor curnew = tripcollection.find(new BasicDBObject("r_utaid",riderutaid));
                    chkcnt=curnew.count();
                    if(chkcnt!=initcnt)
                    {
                        DBCursor cur2 = tripcollection.find(new BasicDBObject("r_utaid",riderutaid));
                        cur2.sort(new BasicDBObject("_id",-1)).limit(1);
                        while (cur2.hasNext()) {

                            BasicDBObject dbobj = (BasicDBObject) cur2.next();
                            tripid = dbobj.get("trip_id").toString();
                            tripstatus=dbobj.get("status").toString();
                            if(tripstatus.equals("Completed")) {
                                Intent i = new Intent(CurrentRide.this, RatingsFeedback.class);
                                i.putExtra("tripid", tripid);
                                i.putExtra("riderid", riderutaid);
                                i.putExtra("driverid", dutaid);
                                startActivity(i);
                                timer.cancel();
                                finish();
                            }else
                            {
                                Intent i2 = new Intent(CurrentRide.this, RequestRide.class);
                                i2.putExtra("utaid",riderutaid);
                                startActivity(i2);
                                timer.cancel();
                                finish();
                            }


                        }

                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(),"Fetching Trip Data...",Toast.LENGTH_SHORT).show();
                    }

                    mongoClient.close();

                }
                catch (Exception e)
                {
                    Log.d("feedback",e.toString());
                }

            }
        };



        timer.scheduleAtFixedRate(timerTask, delay, intervalPeriod);

    }

    @Override
    public void onBackPressed()
    {

    }



}
