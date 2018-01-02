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
import android.widget.RatingBar;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoIncompatibleDriverException;
import com.mongodb.MongoTimeoutException;


public class RatingsFeedback extends AppCompatActivity {

    public RatingBar ratingBar;
    public String DATABASE_NAME = "mav";
    public String COLLECTION_NAME1 = "driver";
    public String COLLECTION_NAME2 = "feedback";
    float existing_rating;
    DBCollection drivercollection;
    DBCollection feedbackcollection;
    public String driver_uta_id,riderutaid,tripid,dutaid,newR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_feedback);

        ratingBar=(RatingBar)findViewById(R.id.driverRatingBar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent i= getIntent();
        riderutaid = i.getStringExtra("riderid");
        tripid=i.getStringExtra("tripid");
        dutaid=i.getStringExtra("driverid");

    }

    public void onSubmit(View view)
    {
        EditText editText = (EditText)findViewById(R.id.EtFeedback);
        String trip_feedback = editText.getText().toString();
        String driver_ratings=String.valueOf(ratingBar.getRating());
        Float current_rating = Float.parseFloat(driver_ratings);

        try {
            MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            DB db = mongoClient.getDB(DATABASE_NAME);
            drivercollection = db.getCollection(COLLECTION_NAME1);
            feedbackcollection = db.getCollection(COLLECTION_NAME2);
            DBCursor cursor = drivercollection.find(new BasicDBObject("d_utaid",dutaid));
                if(cursor.count()>0) {
                    while (cursor.hasNext()) {
                        BasicDBObject bdo = (BasicDBObject)cursor.next();
                        String d_r = (String) bdo.get("d_rating");
                        existing_rating =Float.parseFloat(d_r);
                        if(driver_ratings.equals(""))
                        {
                            float newRating =((existing_rating + existing_rating)/2);
                            newR = Float.toString(newRating);
                        }
                        else{
                            float newRating =((existing_rating + current_rating)/2);
                            newR = Float.toString(newRating);
                        }
                        BasicDBObject setratings = new BasicDBObject("$set", new BasicDBObject("d_rating", newR));
                        drivercollection.update(new BasicDBObject("d_utaid",dutaid), setratings);
                    }
                }
                if(trip_feedback.equals(" "))
                {
                    BasicDBObject tfeedback = new BasicDBObject("r_utaid",riderutaid).append("trip_id",tripid).append("d_utaid",dutaid).append("feedback","-");
                    feedbackcollection.insert(tfeedback);
                }
                else
                {
                    BasicDBObject tfeedback = new BasicDBObject("r_utaid",riderutaid).append("trip_id",tripid).append("d_utaid",dutaid).append("feedback",trip_feedback);
                    feedbackcollection.insert(tfeedback);
                }

                Toast.makeText(getApplicationContext(),"Success !",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RatingsFeedback.this,RequestRide.class);
                intent.putExtra("utaid",riderutaid);
                startActivity(intent);



        }
        catch (MongoTimeoutException mte) {
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

}

