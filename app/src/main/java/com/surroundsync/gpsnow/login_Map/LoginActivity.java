package com.surroundsync.gpsnow.login_Map;

import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.surroundsync.gpsnow.R;
import com.surroundsync.gpsnow.registration.NewUserRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements LocationListener,View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private String stringLatitude = null;
    private String stringLongitude = null;
    String passwordFromServer, userNameFromServer, nameFromServer;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;
    boolean loginStatus = false;
    private Criteria criteria;
    private List<Address> address;
    private double latitude;
    private double longitude;
    private DatabaseReference mDatabase;
    DatabaseReference userChildRef;
    List<String> blockUser;
    public List<String> allUsersBlockedlist;
    private String allUsersId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignUp = (Button) findViewById(R.id.activity_btn_signup_main);
        etUsername = (EditText) findViewById(R.id.activity_username_et_login);
        etPassword = (EditText) findViewById(R.id.activity_pass_et_login);
        btnLogin = (Button) findViewById(R.id.activity_btn_login_ma);
        btnSignUp.setOnClickListener(this);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        blockUser = new ArrayList<String>();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        Log.d("locationlog", "location :" + location);
        if (location != null) {
            location = locationManager.getLastKnownLocation(provider);
            onLocationChanged(location);
        } else {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {

                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if (bestLocation == null) {
                bestLocation = null;
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
            location = bestLocation;
            Log.d("locationlog", "location :" + location);
            latitude = location.getLatitude();
            stringLatitude = String.valueOf(latitude);
            longitude = location.getLongitude();
            stringLongitude = String.valueOf(longitude);
        }
        

        btnLogin.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final String userName = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                userChildRef = mDatabase.child("gpsnow");
                fetchingDataFromFirebase();
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
                                        //query for checking weather the user is a new user or a already logged in user
                                        Query query = userChildRef.child("login").child(userName);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.getValue()== null) {

                                                    addUserToBlock(userName);
                                                    loginStatus = true;

                                                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                                    HashMap<String, Object> result = new HashMap<>();
                                                    result.put("username", userName);
                                                    result.put("latitude", stringLatitude);
                                                    result.put("longitude", stringLongitude);
                                                    result.put("name", nameFromServer);
                                                    result.put("status", loginStatus);
                                                    result.put("blocked", blockUser);
                                                    userChildRef.child("login").child(userName).setValue(result);

                                                    Intent intent = new Intent(getBaseContext(), MapActivity.class);
                                                    intent.putExtra("username", userName);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                                                } else {
                                                    loginStatus = true;
                                                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                                    HashMap<String, Object> result = new HashMap<>();
                                                    result.put("latitude", stringLatitude);
                                                    result.put("longitude", stringLongitude);
                                                    result.put("status", loginStatus);
                                                    userChildRef.child("login").child(userName).updateChildren(result);

                                                    Intent intent = new Intent(getBaseContext(), MapActivity.class);
                                                    intent.putExtra("username", userName);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();

                                    }


                                } else {
                                    Toast.makeText(LoginActivity.this, "your user name is not registered.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });

            }

        });
    }

    private void addUserToBlock(final String userName) {
        userChildRef = mDatabase.child("gpsnow");
        userChildRef.child("login").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().toString().equals(userName)) {
                        Log.d("snapshot", "" + snapshot);
                        allUsersId = snapshot.child("username").getValue().toString();
                        Log.d("alluserid", "" + allUsersId);
                        GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {
                        };
                        allUsersBlockedlist = snapshot.child("blocked").getValue(typeIndicator);
                        Log.d("blocklist", "list" + allUsersBlockedlist);

                        if (!allUsersBlockedlist.contains(userName)) {
                            Log.d("insideblocklist", "" + allUsersBlockedlist);
                            Map<String, Object> map = new HashMap<>();
                            allUsersBlockedlist.add(userName);
                            map.put("blocked", allUsersBlockedlist);
                            userChildRef.child("login").child(allUsersId).updateChildren(map);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        stringLatitude = String.valueOf(latitude);
        longitude = location.getLongitude();
        stringLongitude = String.valueOf(longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

//fetching all login users
    private void fetchingDataFromFirebase() {

        userChildRef = mDatabase.child("gpsnow");
        userChildRef.child("login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     String userId = snapshot.child("username").getValue().toString();
                     blockUser.add(userId);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), NewUserRegistration.class);
        startActivity(intent);
    }
}