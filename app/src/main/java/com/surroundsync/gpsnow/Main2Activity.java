package com.surroundsync.gpsnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private List<Address> address;
    private String subLocation;
    private Location location;
    private DatabaseReference mDatabase;
    private String userName;
    private static DatabaseReference userChildRef;
    private String name = null;
    private String latitude = null;
    private String longitude = null;
    private boolean status = false;
    private String usersId;
    private double x, y = 0.0;
    private Marker myMarker;
    private NavigationView navigationView;
    private List<String> list;
    public static ArrayList<Users> MapUsers = new ArrayList<Users>();
    public static ArrayList<Users> MapusersId = new ArrayList<Users>();
    public String registerduserID;
    private String name_of_mapUsers;
    private List<UserDetails> userDetailsList = new ArrayList<>();
    private ArrayList<String> registeredUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userName = getIntent().getStringExtra("username");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        mMap = mapFragment.getMap();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
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

        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            subLocation = address.get(0).getSubLocality();
            setTitle(subLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void addItemsToDrawer(List<UserDetails> list) {

        MapUsers.clear();
        Menu m = navigationView.getMenu();
        SubMenu users = m.addSubMenu("Users");

        for (final UserDetails details : list) {
            registerduserID = details.getUserId();
            name_of_mapUsers = details.getName();
            MapUsers.add(new Users(name_of_mapUsers, registerduserID));
            registeredUsers.add(registerduserID);
            users.add(details.getName()).setTitle(details.getName()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(Main2Activity.this, " title : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(details.getLatitude()), Double.parseDouble(details.getLongitude())), 20.0f));
                    return false;
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), UserContent.class);
            intent.putExtra("username", userName);
            startActivity(intent);
            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            SharedPreferences sharedpreferences = getSharedPreferences("Logout.MyPREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            userChildRef = mDatabase.child("gpsnow");
            userChildRef.child("login").child(userName).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("status", false);
                                userChildRef.child("login").child(userName).updateChildren(result);
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        loginUser();
        fetchingDataFromFirebase();

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    public void loginUser() {

        userChildRef = mDatabase.child("gpsnow").child("login");
        userChildRef.child(userName).child("blocked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {
                };
                list = dataSnapshot.getValue(typeIndicator);
                showMap(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMap(final List<String> blockL) {
        userChildRef = mDatabase.child("gpsnow");
        userChildRef.child("login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    name = snapshot.child("name").getValue().toString();
                    longitude = snapshot.child("longitude").getValue().toString();
                    latitude = snapshot.child("latitude").getValue().toString();
                    status = (boolean) snapshot.child("status").getValue();
                    usersId = snapshot.child("username").getValue().toString();
                    x = Double.parseDouble(latitude);
                    y = Double.parseDouble(longitude);

                    UserDetails object = new UserDetails(name, latitude, longitude, status, usersId);
                    userDetailsList.add(object);
                    createMarker(x, y, name, usersId, status, blockL);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetchingDataFromFirebase() {

        userChildRef = mDatabase.child("gpsnow");
        userChildRef.child("login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    name = snapshot.child("name").getValue().toString();
                    longitude = snapshot.child("longitude").getValue().toString();
                    latitude = snapshot.child("latitude").getValue().toString();
                    status = (boolean) snapshot.child("status").getValue();
                    usersId = snapshot.child("username").getValue().toString();
                    String a = snapshot.child("blocked").getValue().toString();
                    x = Double.parseDouble(latitude);
                    y = Double.parseDouble(longitude);
                    UserDetails object = new UserDetails(name, latitude, longitude, status, usersId);
                    userDetailsList.add(object);

                }

                addItemsToDrawer(userDetailsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createMarker(double x, double y, String name, String usersID, boolean Status, List<String> listBlock) {
        for (String listofBlockedUsers : listBlock) {

            if (listofBlockedUsers.equals(usersID)) {

                mMap.addMarker(new MarkerOptions().position(new LatLng(x, y)).anchor(0.5f, 0.5f).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            }
        }
        return;

    }


    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        //mMap.clear();
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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

