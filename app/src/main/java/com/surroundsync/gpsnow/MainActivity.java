package com.surroundsync.gpsnow;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private String stringLatitude = null;
    private String stringLongitude = null;
    private String gpsLocation =null;

    String passwordFromServer, userNameFromServer, nameFromServer;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;
    boolean loginStatus = false;
    private Criteria criteria;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignUp = (Button) findViewById(R.id.activity_btn_signup_main);
        etUsername = (EditText) findViewById(R.id.activity_username_et_login);
        etPassword = (EditText) findViewById(R.id.activity_pass_et_login);
        btnLogin = (Button) findViewById(R.id.activity_btn_login_ma);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        /*btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });*/

        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        criteria=new Criteria();
        provider=locationManager.getBestProvider(criteria,false);
        location=locationManager.getLastKnownLocation(provider);
        double lattitude=location.getLatitude();
        double longitude=location.getLongitude();
        stringLatitude=String.valueOf(lattitude);
        stringLongitude=String.valueOf(longitude);

        btnLogin.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final String userName = etUsername.getText().toString();
                final String password = etPassword.getText().toString();


                final DatabaseReference userChildRef = mDatabase.child("gpsnow");

                userChildRef.child("users").child(userName).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() != null) {
                                    // Get user value

                                    userNameFromServer = (String) dataSnapshot.child("username").getValue();
                                    passwordFromServer = (String) dataSnapshot.child("password").getValue();
                                    nameFromServer = (String) dataSnapshot.child("name").getValue();
                                    if (passwordFromServer.equals(password)) {
                                        loginStatus = true;
                                        Toast.makeText(MainActivity.this, "validation success.", Toast.LENGTH_SHORT).show();
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("username", userName);
                                        result.put("latitude",stringLatitude);
                                        result.put("longitude",stringLongitude);
                                        result.put("name",nameFromServer);
                                        result.put("status",loginStatus);
                                        userChildRef.child("login").child(userName).setValue(result);
                                        Intent intent= new Intent(getBaseContext(),Main2Activity.class);
                                        intent.putExtra("username",userName);
                                        startActivity(intent);


                                    } else {
                                        Toast.makeText(MainActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();

                                    }


                                } else {
                                    Toast.makeText(MainActivity.this, "your user name is not registered.", Toast.LENGTH_SHORT).show();
                                }

                            }


                            // ...


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
                /*Query myTopPostsQuery = DatabaseReference.child("user-posts").child(myUserId)
                        .orderByChild("starCount");

                userChildRef.addListenerForSingleValueEvent(new ChildEventListener() {
                    @Override
                    private void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Value", "" + dataSnapshot.getValue());
                        if (dataSnapshot.child(email).exists()) {
                            DataSnapshot userSnapshot = (DataSnapshot) dataSnapshot.getChildren().iterator().next();

                            emailFromServer = (String) userSnapshot.child("email").getValue();
                            passwordFromServer = (String) userSnapshot.child("password").getValue();

                            if (password.equals(passwordFromServer)) {
                                Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "your email is not registered.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


            }

        });
    }

}
