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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoIncompatibleDriverException;
import com.mongodb.MongoTimeoutException;

import java.util.Timer;
import java.util.TimerTask;

public class WaitingScreen extends AppCompatActivity {

    String riderutaid,dutaid;
    DBCollection requestcollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent wfromr=getIntent();
        riderutaid=wfromr.getStringExtra("rid");

        final Timer timer=new Timer();
        long delay=10;
        long intervalPeriod = 2000;
        TimerTask timerTask=new TimerTask() {
            @Override

            public void run() {

                String reqstatus,src,dest,noriders;
                try{
                    MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                    MongoClient mongoClient = new MongoClient(mongoClientURI);
                    DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                    requestcollection = db.getCollection("request");
                    DBCursor cur = requestcollection.find(new BasicDBObject("r_utaid",riderutaid).append("status","assigned"));
                    if(cur.count()>0){

                        while (cur.hasNext()) {
                            BasicDBObject dbobj = (BasicDBObject) cur.next();
                            reqstatus = dbobj.get("status").toString();
                            src = dbobj.get("source").toString();
                            dest = dbobj.get("destination").toString();
                            noriders = dbobj.get("no_of_riders").toString();
                            dutaid = dbobj.get("d_utaid").toString();

                           // notify();
                            Intent i = new Intent(WaitingScreen.this, CurrentRide.class);
                                i.putExtra("riderid", riderutaid);
                                i.putExtra("src", src);
                                i.putExtra("dest", dest);
                                i.putExtra("noofriders", noriders);
                                i.putExtra("dutaid", dutaid);
                            NotifyNow(src,dest,noriders,dutaid);
                                startActivity(i);
                                timer.cancel();



                        }

                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(),"Looking for Drivers...",Toast.LENGTH_SHORT).show();
                    }

                    mongoClient.close();

                }
                catch (Exception e)
                {
                    Log.d("Assigned",e.toString());
                }

            }
        };



        timer.scheduleAtFixedRate(timerTask, delay, intervalPeriod);

    }


    public void onCancel(View v)
    {
        try {

                BasicDBObject bdo = new BasicDBObject("r_utaid", riderutaid);
                MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                requestcollection = db.getCollection("request");
                DBCursor cur1=requestcollection.find(new BasicDBObject("r_utaid",riderutaid).append("status","waiting"));
                if (cur1.count() > 0) {
                    requestcollection.remove(new BasicDBObject("r_utaid",riderutaid));
                        Intent i4=new Intent(WaitingScreen.this,RequestRide.class);
                        i4.putExtra("utaid",riderutaid);
                        startActivity(i4);
                } else {
                    Toast.makeText(getApplicationContext(), "Ride has been assigned.\nPlease Wait...", Toast.LENGTH_LONG).show();
            }


        } catch (MongoTimeoutException mte) {
            mte.printStackTrace();
        } catch (MongoIncompatibleDriverException mide) {
            mide.printStackTrace();
        } catch (Exception exe) {
            exe.printStackTrace();
        }

    }

    @Override
    public void onBackPressed()
    {

    }

    void NotifyNow(String src,String dest,String noriders,String dutaid) {
        // Log.d(TAG, "Notify: ");

        Intent notificationIntent = new Intent(this, CurrentRide.class);
        notificationIntent.putExtra("riderid", riderutaid);
        notificationIntent.putExtra("src", src);
        notificationIntent.putExtra("dest", dest);
        notificationIntent.putExtra("noofriders", noriders);
        notificationIntent.putExtra("dutaid", dutaid);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Ride Has been Assigned")
                        .setContentText("Please wait at the pickup location");
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());


    }

}
