package com.surroundsync.gpsnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class UserContent extends AppCompatActivity {

    ArrayList<Users> users = new ArrayList<>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_listview);

        users = Main2Activity.MapUsers;

        Log.d("List", "MapUsers"+users);

        final UserHandle adapter = new UserHandle(this,users);

        listView =(ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }


}
