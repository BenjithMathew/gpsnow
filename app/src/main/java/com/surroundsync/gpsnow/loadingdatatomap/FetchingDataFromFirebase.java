package com.surroundsync.gpsnow.loadingdatatomap;

import android.provider.ContactsContract;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.surroundsync.gpsnow.googleplus.GoolePlus;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devil on 14-06-2016.
 */
public class FetchingDataFromFirebase {

public FetchingDataFromFirebase(){

}

    GoolePlus google;

    UserDetails user;

    public String TAG = "UserDetails";

    public String name =null;
    public String longitude =null;
    public String latitude = null;
    public boolean status = false;

    ArrayList<UserDetails> userArray = new ArrayList<UserDetails>();


    public  void firebaseData(){

        google.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //Log.d(TAG, "snapshot : " + snapshot.getValue());

                    name = snapshot.child("name").getValue().toString();
                    longitude =snapshot.child("longitude").toString();
                    latitude = snapshot.child("latitude").toString();
                    status = (boolean)snapshot.child("status").getValue();

                    user = new UserDetails(name,latitude,longitude,status);

                    Log.d(TAG,"user details" + " "+"name" +name +" "+"longitude"+ longitude+"latitude"+latitude+" "+"status"+status+"");

                    userArray.add(user);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
