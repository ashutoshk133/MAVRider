package com.techwarriors.mav_rider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoIncompatibleDriverException;
import com.mongodb.MongoTimeoutException;

public class RequestRide extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String riderutaid, source, destination, noriders;
    Spinner sourcespinner, destinationspinner, riderspinner;
    DBCollection requestcollection;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp=getSharedPreferences("data",MODE_PRIVATE);
        riderutaid=sp.getString("rid", null);

        /*Intent i2 = getIntent();
        riderutaid = i2.getStringExtra("utaid");*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sourcespinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> sourceadapter = ArrayAdapter.createFromResource(this, R.array.sourcearray, android.R.layout.simple_spinner_item);
        sourceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourcespinner.setAdapter(sourceadapter);
        sourcespinner.setSelection(0);

        destinationspinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> destinationadapter = ArrayAdapter.createFromResource(this, R.array.destinationarray, android.R.layout.simple_spinner_item);
        destinationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationspinner.setAdapter(destinationadapter);
        destinationspinner.setSelection(0);

        riderspinner = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> rideradapter = ArrayAdapter.createFromResource(this, R.array.riderarray, android.R.layout.simple_spinner_item);
        rideradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riderspinner.setAdapter(rideradapter);
        riderspinner.setSelection(0);


        class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        }

    }


    public void onRequest(View v) {
        try {

            source = sourcespinner.getSelectedItem().toString();
            destination = destinationspinner.getSelectedItem().toString();
            noriders = riderspinner.getSelectedItem().toString();

            if (source.equals("Select Source") || destination.equals("Select Destination") || noriders.equals("Select Riders")) {
                Snackbar.make(v, "Error : Please Select All Fields", Snackbar.LENGTH_SHORT).show();
            } else {
                if(source.equals(destination))
                {
                    Snackbar.make(v,"Error : Same Source and Destination",Snackbar.LENGTH_SHORT).show();
                }
                else{


                    BasicDBObject bdo = new BasicDBObject("r_utaid", riderutaid).append("source", source).append("destination", destination).append("no_of_riders", noriders).append("status", "waiting");
                    MongoClientURI mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                    MongoClient mongoClient = new MongoClient(mongoClientURI);
                    DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                    requestcollection = db.getCollection("request");
                    requestcollection.insert(bdo);
                    mongoClient.close();
                    Toast.makeText(this, "Request Placed !", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(this, WaitingScreen.class);
                    i.putExtra("rid", riderutaid);
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


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }


    @SuppressLint("MissingPermission")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent i = new Intent(RequestRide.this, EditProfileDetails.class);
            i.putExtra("rutaid", riderutaid);
            startActivity(i);

        } else if (id == R.id.nav_req) {

            Intent nav_intent = new Intent(this,RequestRide.class);
            startActivity(nav_intent);

        } else if (id == R.id.nav_SOS) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:12345"));
             startActivity(callIntent);

        } else if (id == R.id.nav_helpline) {

            Intent callIntent2 = new Intent(Intent.ACTION_CALL);
            callIntent2.setData(Uri.parse("tel:8172725252"));
            startActivity(callIntent2);

        } else if (id == R.id.nav_logout) {

            sp=getSharedPreferences("data",MODE_PRIVATE);

            SharedPreferences.Editor editor=sp.edit();
            editor.putInt("isLogged", 0);
            editor.commit();
            Intent i =new Intent(RequestRide.this,Login.class);
            startActivity(i);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
