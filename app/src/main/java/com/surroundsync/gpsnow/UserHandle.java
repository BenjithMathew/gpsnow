package com.surroundsync.gpsnow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class UserHandle extends ArrayAdapter<Users> {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userChildRef = mDatabase.child("gpsnow").child("login");


    ArrayList<String> blockedUserIds = new ArrayList<>();
    private ArrayList<String> blockedIds = new ArrayList<>();

    String idName = null;



    public UserHandle(Context context, ArrayList<Users> users) {
        super(context, R.layout.custom_row_listview, users);

    }

    static class UserHolder {
        TextView tv;
        ToggleButton tb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Users user = getItem(position);
        final UserHolder userHolder;

        if (convertView == null) {
            userHolder = new UserHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_row_listview, parent, false);
            userHolder.tv = (TextView) convertView.findViewById(R.id.username);
            userHolder.tb = (ToggleButton) convertView.findViewById(R.id.toggleButton);
            convertView.setTag(userHolder);

        } else {
            userHolder = (UserHolder) convertView.getTag();
        }
        userHolder.tv.setText(user.name);
        userHolder.tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    /*SharedPreferences sharedpreferences = getSharedPreference("Logout.MyPREFERENCES", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();*/
                   // editor.commit();
                    idName = user.getUserId();
                    Log.d("idname", idName.toString());
                    dontShowMapLocation(idName);



                } else {
                    idName = user.getUserId();
                    showMapLocation(idName);

                }
            }
        });
        return convertView;
    }

    public void dontShowMapLocation(final String userId) {

        userChildRef.child(userId).child("blocked").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {
                };
                blockedUserIds = mutableData.getValue(typeIndicator);
                if (blockedUserIds.contains(UserContent.userName)) {
                    blockedUserIds.remove(UserContent.userName);
                }
                mutableData.setValue(blockedUserIds);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

    }

    public void showMapLocation(final String userId) {

        userChildRef.child(userId).child("blocked").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {
                };
                blockedIds = mutableData.getValue(typeIndicator);
                Log.d("Condition", "value : " + blockedIds + " condition : " + blockedIds.contains(UserContent.userName));
                if (!blockedIds.contains(UserContent.userName)) {
                    blockedIds.add(UserContent.userName);
                }

                mutableData.setValue(blockedIds);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


}
