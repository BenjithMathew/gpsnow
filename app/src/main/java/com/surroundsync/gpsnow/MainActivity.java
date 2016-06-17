package com.surroundsync.gpsnow;

import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "EmailPassword";
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private String stringLatitude = null;
    private String stringLongitude = null;
    List<String> listBlock;
    String passwordFromServer, userNameFromServer, nameFromServer;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;
    boolean loginStatus = false;
    private Criteria criteria;
    private Geocoder geocoder;
    private List<Address> address;
    private double latitude;
    private double longitude;
    private DatabaseReference mDatabase;
    String myAddress;
    String city;
    String state;
    String country;
    String knownArea;
    String subLocation;
    DatabaseReference userChildRef, ref;
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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        blockUser = new ArrayList<String>();



        /*btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });*/

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
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address = geocoder.getFromLocation(latitude, longitude, 1);

            myAddress = address.get(0).getAddressLine(0);
            city = address.get(0).getLocality();
            state = address.get(0).getAdminArea();
            country = address.get(0).getCountryName();
            knownArea = address.get(0).getFeatureName();
            subLocation = address.get(0).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
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

                                        Query query = userChildRef.child("login").child(userName);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.getValue() == null) {

                                                    addUserToBlock(userName);

                                                    loginStatus = true;
                                                    Toast.makeText(MainActivity.this, "validation success.", Toast.LENGTH_SHORT).show();
                                                    HashMap<String, Object> result = new HashMap<>();
                                                    result.put("username", userName);
                                                    result.put("latitude", stringLatitude);
                                                    result.put("longitude", stringLongitude);
                                                    result.put("name", nameFromServer);
                                                    result.put("status", loginStatus);
                                                    result.put("blocked", blockUser);
                                                    Log.d("blocked users", " all firebase userID" + blockUser);
                                                    userChildRef.child("login").child(userName).setValue(result);
                                                    Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                                    intent.putExtra("username", userName);
                                                    Log.d("Start Map Activity", "intent to start");
                                                    startActivity(intent);
                                                    Log.d("Start Map Activity", "intent started");
                                                } else {
                                                    loginStatus = true;
                                                    Toast.makeText(MainActivity.this, "validation success.", Toast.LENGTH_SHORT).show();
                                                    HashMap<String, Object> result = new HashMap<>();
                                                    result.put("latitude", stringLatitude);
                                                    result.put("longitude", stringLongitude);
                                                    result.put("status", loginStatus);
                                                    userChildRef.child("login").child(userName).updateChildren(result);
                                                    Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                                    intent.putExtra("username", userName);
                                                    startActivity(intent);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });

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

                        if(!allUsersBlockedlist.contains(userName)) {
                            Log.d("insideblocklist",""+allUsersBlockedlist);
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

    private void fetchingDataFromFirebase() {

        userChildRef = mDatabase.child("gpsnow");
        userChildRef.child("login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Log.d("data", " value : " + dataSnapshot.getValue());
                    String name = snapshot.child("name").getValue().toString();
                    String longitude = snapshot.child("longitude").getValue().toString();
                    String latitude = snapshot.child("latitude").getValue().toString();
                    boolean status = (boolean) snapshot.child("status").getValue();
                    String userId = snapshot.child("username").getValue().toString();

                    double x = Double.parseDouble(latitude);
                    double y = Double.parseDouble(longitude);

                    blockUser.add(userId);

                }
                Log.d("block user", " blockuser: " + blockUser.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}