package com.surroundsync.gpsnow.registration;

/**
 * Created by Benjith PC on 09-Jun-16.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.surroundsync.gpsnow.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserRegistration extends Activity implements View.OnClickListener {

    EditText etName, etUserName, etEmail, etPassword;

    Button btnSave;
    private static boolean flag = false;
    String name;
    String userName;
    String email;
    String password;
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = rootReference.child("gpsnow");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);
        Firebase.setAndroidContext(this);

        etName = (EditText) findViewById(R.id.activity_new_user_registartion_et_name);
        etEmail = (EditText) findViewById(R.id.activity_new_user_registartion_et_email);
        etUserName = (EditText)findViewById(R.id.activity_new_user_registartion_et_username);
        etPassword = (EditText) findViewById(R.id.activity_new_user_registartion_et_password);
        pattern = Pattern.compile(PASSWORD_PATTERN);


        btnSave = (Button) findViewById(R.id.activity_new_user_registartion_btn_save);

        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        name = etName.getText().toString();
        userName = etUserName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (isValid()) {
            valid();
        } else {
            Toast.makeText(NewUserRegistration.this, "All fields are Mandratory..! ", Toast.LENGTH_SHORT).show();
            etName.setText("");
            etEmail.setText("");
            etPassword.setText("");
            etUserName.setText("");
        }


    }


    //validation of all feilds in the registration
    private boolean isValid() {
        flag = false;
        matcher = pattern.matcher(password);
        if (name.equals("")) {
            etName.setError("Enter your name..!");
            flag = false;
        }else if (userName.equals("")) {
            etUserName.setError("Enter a user name..!");
            flag = false;
        }
        else if (email.equals("")) {
            etEmail.setError("Enter your emailId..!");
            flag = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email is not valid");
            etEmail.setText("");
            flag = false;
        } else if (password.equals("")) {
            etPassword.setError("Enter a password");
            flag = false;
        } else if (!matcher.matches()) {
            etPassword.setError("Password have 6-20 characters,and it contains at least one [a-z] ,at least one [A-Z] ,at least one [0-9] and contains one special symbols @,#,$,%");
            etEmail.setText("");
             flag = false;

        } else {
            flag = true;
        }

        return flag;
    }


    //adding new user data to the firebase
    public void valid() {
        Query query = ref.child("users").child(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    etUserName.setError("User name already exist");
                    etUserName.setText("");
                    etPassword.setText("");
                    Toast.makeText(NewUserRegistration.this, "User Name entered is already registered in database. go to login page.", Toast.LENGTH_SHORT).show();

                }else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("username", userName);
                    map.put("email", email);
                    map.put("password", password);
                    ref.child("users").child(userName).setValue(map);

                    etName.setText("");
                    etEmail.setText("");
                    etPassword.setText("");
                    etUserName.setText("");
                    Toast.makeText(NewUserRegistration.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

