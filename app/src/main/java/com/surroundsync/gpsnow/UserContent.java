package com.surroundsync.gpsnow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class UserContent extends AppCompatActivity implements  Cloneable{

    public ArrayList<Users> users = new ArrayList<>();
    public ArrayList<Users> usersIds = new ArrayList<>();

    private ListView lvListView;
    private ToggleButton btnToggle;
    public static String userName;

    SharedPreferences sharedPreferences;

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

        sharedPreferences =getSharedPreferences("Logout.MyPREFERENCES", Context.MODE_PRIVATE);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }
}
