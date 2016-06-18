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

    private ArrayList<Users> users = new ArrayList<>();
    private ArrayList<Users> usersIds = new ArrayList<>();

    private ListView lvListView;
    private ToggleButton btnToggle;
    private static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_listview);
        userName = getIntent().getStringExtra("username");
        users = Main2Activity.MapUsers;
        usersIds = Main2Activity.MapusersId;
        final UserHandle adapter = new UserHandle(this, users);
        btnToggle = (ToggleButton) findViewById(R.id.toggleButton);
        lvListView =(ListView)findViewById(R.id.listView);
        lvListView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }
}
