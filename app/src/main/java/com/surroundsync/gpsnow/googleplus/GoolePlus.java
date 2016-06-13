package com.surroundsync.gpsnow.googleplus;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.surroundsync.gpsnow.R;

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

public class GoolePlus extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int RC_GOOOGLE_SIGN_IN = 546;
    ;

    /*DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference ref = mRootRef.child("gpsnow");*/

    Firebase ref;

    private static final String TAG = "SignInActivity";

    private GoogleApiClient mGoogleApiClient;

    private SignInButton googleplus_signin_button;

    private Button googleplus_signout_button;

    private EditText etGooglename, etGoogleemail, etGoogleID;

    private ProgressDialog mProgressDialog;

    private double currentLatitude;

    private double currentLongitude;

    private String latitude =null;

    private String longitude = null;

    boolean status = false;

    String userId = null;

    String displayName = null;

    private LocationManager locationManager;

    private Location location;

    private Criteria criteria;

    private String provider;

    private boolean isGPSenabled;

    boolean isNetworkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.content_goole_plus);


        ref = new Firebase("https://gpstodo.firebaseio.com/gpsnow/login");
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        //--------------for signiin button---------
        googleplus_signin_button = (SignInButton) findViewById(R.id.activity_googleplus_btn_signin);

        googleplus_signin_button.setSize(SignInButton.SIZE_STANDARD);
        googleplus_signin_button.setColorScheme(SignInButton.COLOR_DARK);
        googleplus_signin_button.setScopes(gso.getScopeArray());

        //-----------------------

        googleplus_signout_button = (Button) findViewById(R.id.activity_googleplus_btn_signout);
        etGooglename = (EditText) findViewById(R.id.activity_etgplus_name);
        etGoogleemail = (EditText) findViewById(R.id.activity_etgplus_email);
        etGoogleID = (EditText) findViewById(R.id.activity_etgplus_ID);


        googleplus_signin_button.setOnClickListener(this);
        googleplus_signout_button.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        //mGoogleApiClient.connect();
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.activity_googleplus_btn_signin:
                googleSignIn();
                break;

            case R.id.activity_googleplus_btn_signout:
                googleSignOut();
        }
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
        etGooglename.setText("");
        etGoogleemail.setText("");
        etGoogleID.setText("");

        status = false;

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> loadData = new HashMap<String, Object>();

                loadData.put("latitude", latitude);
                loadData.put("longitude", longitude);
                loadData.put("name", displayName);
                loadData.put("source", "g+");
                loadData.put("status", status);
                loadData.put("userId", userId);

                ref.child(userId).setValue(loadData);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOOGLE_SIGN_IN);
        location();
    }


    private void updateUI(boolean isSignedIn) {

        if (isSignedIn) {
            googleplus_signin_button.setVisibility(View.GONE);
            googleplus_signout_button.setVisibility(View.VISIBLE);

        } else {
            googleplus_signin_button.setVisibility(View.VISIBLE);
            googleplus_signout_button.setVisibility(View.GONE);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            etGooglename.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            etGoogleemail.setText(acct.getEmail());
            etGoogleID.setText(acct.getId());

            status = true;

            userId = acct.getId();

            displayName = acct.getDisplayName();

            //final Query query = ref.child("login");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> loadData = new HashMap<String, Object>();

                    loadData.put("latitude", latitude);
                    loadData.put("longitude", longitude);
                    loadData.put("name", displayName);
                    loadData.put("source", "g+");
                    loadData.put("status", status);
                    loadData.put("userId", acct.getId());

                    ref.child(acct.getId()).setValue(loadData);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }


    public void location(){

        if(!isGPSenabled){
            Toast.makeText(GoolePlus.this, "Location is not available !! Turn on GPS ", Toast.LENGTH_SHORT).show();
        }else if(!isNetworkEnabled){
            Toast.makeText(GoolePlus.this, "Network is currently not available", Toast.LENGTH_SHORT).show();
        }else{
            currentLatitude = location.getLatitude();

            latitude = String.valueOf(currentLatitude);

            currentLongitude = location.getLongitude();

            longitude = String.valueOf(currentLongitude);
        }
    }
}