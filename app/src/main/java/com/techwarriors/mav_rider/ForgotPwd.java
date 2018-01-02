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

public class ForgotPwd extends AppCompatActivity {

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection ridercollection;
    final static String COLLECTION_NAME1="rider";
    String secque,secans;

    String utaid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final EditText etutaid=(EditText)findViewById(R.id.UTAID);

        Button next=(Button)findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utaid=etutaid.getText().toString();

                mongoClientURI = new MongoClientURI(Connectivity.DATABASE_URI);
                mongoClient = new MongoClient(mongoClientURI);
                DB db = mongoClient.getDB(Connectivity.DATABASE_NAME);
                ridercollection = db.getCollection(COLLECTION_NAME1);
                DBCursor cur=ridercollection.find(new BasicDBObject("r_utaid",utaid));

                if(cur.count()>0)
                {
                    while (cur.hasNext())
                    {
                        BasicDBObject dbObject=(BasicDBObject)cur.next();
                        secque=dbObject.get("r_sec_que").toString();
                        secans=dbObject.get("r_sec_ans").toString();
                    }
                    Intent intent=new Intent(ForgotPwd.this,ConfirmAns.class);
                    intent.putExtra("rid",utaid);
                    intent.putExtra("secque",secque);
                    intent.putExtra("secans",secans);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Details!",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

}
