package com.surroundsync.gpsnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class UserContent extends AppCompatActivity implements  Cloneable{

    ArrayList<Users> users = new ArrayList<>();
    ArrayList<Users> usersIds = new ArrayList<>();

    ListView lvListView;
    ToggleButton btnToggle;
    static String userName;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference myRef = rootRef.child("gpsnow");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_listview);
        userName = getIntent().getStringExtra("username");
        Log.d("Users","username"+userName);

        users = Main2Activity.MapUsers;
        usersIds = Main2Activity.MapusersId;

        Log.d("List", "MapUsers"+users);

        final UserHandle adapter = new UserHandle(this, users);

        btnToggle = (ToggleButton) findViewById(R.id.toggleButton);
        lvListView =(ListView)findViewById(R.id.listView);
        lvListView.setAdapter(adapter);

    }





}
