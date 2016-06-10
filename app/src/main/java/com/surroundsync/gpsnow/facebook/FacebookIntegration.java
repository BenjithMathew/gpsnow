package com.surroundsync.gpsnow.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surroundsync.gpsnow.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class FacebookIntegration extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = rootReference.child("gpsnow");
    GpsTracker gpsTracker;
    String stringLatitude=null;
    String stringLongitude=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_facebook_integration);
        callbackManager = CallbackManager.Factory.create();
        Firebase.setAndroidContext(this);
        gpsTracker = new GpsTracker(this);
        locationDetails();

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
                            locationDetails();
                            String userId = loginResult.getAccessToken().getUserId();
                            String userName = jsonObject.getString("name").toString();
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", userName);
                            map.put("userId", userId);
                            map.put("source", "fb");
                            map.put("status", true);
                            map.put("latitude", stringLatitude);
                            map.put("longitude", stringLongitude);
                            ref.child("login").child(userId).setValue(map);

                            // JSONObject data = graphResponse.getJSONObject();
                            //profilePictureView.setProfileId(userId);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        info.setText("");
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
//TO get the location details
    public void locationDetails() {
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            stringLatitude = String.valueOf(gpsTracker.latitude);

            stringLongitude = String.valueOf(gpsTracker.longitude);

            // String country = gpsTracker.getCountryName(this);
            // String city = gpsTracker.getLocality(this);
            //String postalCode = gpsTracker.getPostalCode(this);
            //String addressLine = gpsTracker.getAddressLine(this);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }


}
