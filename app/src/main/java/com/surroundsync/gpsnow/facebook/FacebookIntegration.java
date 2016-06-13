package com.surroundsync.gpsnow.facebook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.surroundsync.gpsnow.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class FacebookIntegration extends AppCompatActivity implements LocationListener {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ref = rootReference.child("gpsnow");
    private String userId;
    private String userName;
    private LocationManager locationManager;
    private Location location;
    private Criteria criteria;
    private String provider;
    private String currentLatitude = null;
    private String currentLongitude = null;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        setContentView(R.layout.activity_facebook_integration);
        callbackManager = CallbackManager.Factory.create();
        Firebase.setAndroidContext(this);
        fbLoginButton = (LoginButton) findViewById(R.id.activity_facebook_login_button);
        fbLoginButton.setReadPermissions("email");


        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                Bundle params = new Bundle();
                params.putString("fields", "id,email,name,picture");


                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {

                            location();
                            userId = loginResult.getAccessToken().getUserId();
                            userName = jsonObject.getString("name").toString();
                            Query query = ref.child("login").child(userId);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", userName);
                                        map.put("userId", userId);
                                        map.put("source", "fb");
                                        map.put("status", true);
                                        map.put("latitude", currentLatitude);
                                        map.put("longitude", currentLongitude);
                                        ref.child("login").child(userId).setValue(map);
                                    } else {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("latitude", currentLatitude);
                                        map.put("longitude", currentLongitude);
                                        map.put("status", true);
                                        ref.child("login").child(userId).updateChildren(map);
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            fbLoginButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (fbLoginButton.getText().toString().equalsIgnoreCase("Log out")) {

                                        ref.child("login").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name", userName);
                                                map.put("userId", userId);
                                                map.put("source", "fb");
                                                map.put("latitude", currentLatitude);
                                                map.put("longitude", currentLongitude);
                                                map.put("status", false);
                                                ref.child("login").child(userId).setValue(map);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                request.setParameters(params);
                request.executeAsync();
                Toast.makeText(getBaseContext(), "Login Successful", LENGTH_SHORT).show();
            }


            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getBaseContext(), "Error...!", Toast.LENGTH_SHORT).show();
            }


        });


    }

    //To get the location details
    public void location() {
        if (!isNetworkEnabled) {
            Toast.makeText(getBaseContext(), "Network is not available", Toast.LENGTH_SHORT).show();
        } else if (!isGPSEnabled) {
            Toast.makeText(getBaseContext(), "Location is not available..! Turn ON your Location", Toast.LENGTH_SHORT).show();
        } else {

            latitude = location.getLatitude();
            currentLatitude = String.valueOf(latitude);
            longitude = location.getLongitude();
            currentLongitude = String.valueOf(longitude);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        currentLatitude = String.valueOf(latitude);
        longitude = location.getLongitude();
        currentLongitude = String.valueOf(longitude);
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
}