package com.surroundsync.gpsnow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by Ashray Joshi on 16-Jun-16.
 */
public class UserHandle extends ArrayAdapter<Users>{

    public UserHandle(Context context, ArrayList<Users> users) {
        super(context,R.layout.custom_row_listview,users);
    }

    static class UserHolder{
        TextView tv;
        ToggleButton tb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Users user = getItem(position);
        UserHolder userHolder =null;

        if(convertView==null) {
            userHolder = new UserHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_row_listview, parent, false);
            userHolder.tv = (TextView) convertView.findViewById(R.id.username);
            userHolder.tb = (ToggleButton) convertView.findViewById(R.id.toggleButton);
            convertView.setTag(userHolder);
        }else{
            userHolder = (UserHolder)convertView.getTag();
        }
        userHolder.tv.setText(user.name);
        return convertView;
    }
}
