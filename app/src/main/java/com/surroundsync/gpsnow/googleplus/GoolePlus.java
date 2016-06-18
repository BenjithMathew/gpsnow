package com.surroundsync.gpsnow.googleplus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
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
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoolePlus extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int RC_GOOOGLE_SIGN_IN = 546;

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

    String allUsersId = null;

    private List<String> allUsersBlockedlist ;

    private ArrayList<String> loginUserList;

    Firebase childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.content_goole_plus);


        ref = new Firebase("https://gpstodo.firebaseio.com/gpsnow/login");
        childRef = new Firebase("https://gpstodo.firebaseio.com/gpsnow");
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
        if (location != null) {
            location = locationManager.getLastKnownLocation(provider);
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
            location();
        }

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

        allUsersBlockedlist = new ArrayList<>();
        loginUserList = new ArrayList<>();

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
                loadData.put("status", status);


                ref.child(userId).updateChildren(loadData);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void googleSignIn() {

        fetchingAllLoginUserIdList();
        location();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOOGLE_SIGN_IN);

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

            Query query = childRef.child("login").child(userId);


            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if(dataSnapshot.getValue()==null) {

                        Log.d("snapshot", " list " + dataSnapshot);

                        fetchingAllLoginUserIdList();
                        addNewUserToBlock(userId);

                        Map<String, Object> loadData = new HashMap<String, Object>();

                        loadData.put("latitude", latitude);
                        loadData.put("longitude", longitude);
                        loadData.put("name", displayName);
                        loadData.put("source", "g+");
                        loadData.put("status", status);
                        loadData.put("username", userId);
                        loadData.put("blocked", loginUserList);

                       ref.child(userId).setValue(loadData);

                    }else{

                        Map<String, Object> map = new HashMap<>();
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        map.put("status", status);

                        childRef.child("login").child(userId).updateChildren(map);

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    //<---Retrieving all login users data from the firebase

    public void fetchingAllLoginUserIdList() {
        childRef.child("login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loginUserList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey().toString();
                    loginUserList.add(userId);
                }


                Log.d("block user", " blockuser: " + loginUserList);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
        }


    //--->Updating already login users blocked list with new user.

    public void addNewUserToBlock(final String userName) {
        childRef.child("login").addValueEventListener(new ValueEventListener() {

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
                            childRef.child("login").child(allUsersId).updateChildren(map);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
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