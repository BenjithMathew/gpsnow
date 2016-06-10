package com.surroundsync.gpsnow.registration;

/**
 * Created by Benjith PC on 09-Jun-16.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private EditText etName, etUserName, etEmail, etPassword;
    private TextView tvPasswordChecker;
    Button btnSave;
    private static boolean flag = false;
    String name;
    String userName;
    String email;
    String password;
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{5,15})";
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = rootReference.child("gpsnow");
    ProgressBar pbPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);
        Firebase.setAndroidContext(this);


        etName = (EditText) findViewById(R.id.activity_new_user_registartion_et_name);
        etEmail = (EditText) findViewById(R.id.activity_new_user_registartion_et_email);
        etUserName = (EditText) findViewById(R.id.activity_new_user_registartion_et_username);
        etPassword = (EditText) findViewById(R.id.activity_new_user_registartion_et_password);
        tvPasswordChecker = (TextView) findViewById(R.id.activity_new_user_registration_tv_password_checker);
        etPassword.addTextChangedListener(textWatcher);
        pattern = Pattern.compile(PASSWORD_PATTERN);
        pbPassword = (ProgressBar) findViewById(R.id.activity_new_user_registration_pb_progressbar);
        btnSave = (Button) findViewById(R.id.activity_new_user_registartion_btn_save);
        pbPassword.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this);

    }


    //password validation
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String passwordString = etPassword.getText().toString();
            matcher = pattern.matcher(passwordString);
            if (passwordString.equals("")) {
                etPassword.setError(null);
                tvPasswordChecker.setText("");
                pbPassword.setVisibility(View.INVISIBLE);
            } else if (!matcher.matches()) {

                if (passwordString.length() > 15) {
                    etPassword.setError("Password have 5-15 characters,and it contains at least one [a-z] ,at least one [A-Z] ,at least one [0-9] and contains one special symbols @,#,$,%");
                    tvPasswordChecker.setTextColor(Color.RED);
                    tvPasswordChecker.setText("Maximum");
                } else {
                    etPassword.setError("Password have 5-15 characters,and it contains at least one [a-z] ,at least one [A-Z] ,at least one [0-9] and contains one special symbols @,#,$,%");
                    pbPassword.setVisibility(View.VISIBLE);
                    pbPassword.getProgressDrawable().setColorFilter(
                            Color.parseColor("#ff0000"), android.graphics.PorterDuff.Mode.SRC_IN);
                    pbPassword.setProgress(40);
                    tvPasswordChecker.setTextColor(Color.parseColor("#ff0000"));
                    tvPasswordChecker.setText("Weak");

                    // passwordEvaluation(s);
                }
            } else {
                etPassword.setError(null);
                passwordEvaluation(s);
            }

        }
    };

    //password evaluation with seekbar
    public void passwordEvaluation(Editable s) {
        if ((s.length() > 0) && (s.length() <=5)) {
            pbPassword.setVisibility(View.VISIBLE);
            pbPassword.getProgressDrawable().setColorFilter(
                    Color.parseColor("#ff0000"), android.graphics.PorterDuff.Mode.SRC_IN);
            pbPassword.setProgress(40);
            tvPasswordChecker.setTextColor(Color.parseColor("#ff0000"));
            tvPasswordChecker.setText("Weak");
        } else if ((s.length() > 5) && (s.length() <= 10)) {
            pbPassword.setVisibility(View.VISIBLE);
            pbPassword.getProgressDrawable().setColorFilter(
                    Color.parseColor("#116116"), android.graphics.PorterDuff.Mode.SRC_IN);
            pbPassword.setProgress(70);
            tvPasswordChecker.setTextColor(Color.parseColor("#116116"));
            tvPasswordChecker.setText("Strong");
        } else if ((s.length() > 10) && (s.length() <= 15)) {
            pbPassword.setVisibility(View.VISIBLE);
            pbPassword.getProgressDrawable().setColorFilter(
                    Color.parseColor("#00b200"), android.graphics.PorterDuff.Mode.SRC_IN);
            pbPassword.setProgress(100);
            tvPasswordChecker.setTextColor(Color.parseColor("#00b200"));
            tvPasswordChecker.setText("Excellent");
        } else if (s.length() > 15) {
            pbPassword.setVisibility(View.INVISIBLE);
            tvPasswordChecker.setTextColor(Color.RED);
            tvPasswordChecker.setText("Maximum");
        } else {
            pbPassword.setVisibility(View.INVISIBLE);
            tvPasswordChecker.setText("");
        }


    }

    //On click save button
    @Override
    public void onClick(View v) {

        name = etName.getText().toString();
        userName = etUserName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (isValid()) {
            valid();
        } else {
            Toast.makeText(NewUserRegistration.this, "All fields are Mandatory..! ", Toast.LENGTH_SHORT).show();
            etName.setText("");
            etEmail.setText("");
            etPassword.setText("");
            etUserName.setText("");
        }


    }


    //validation of all fields in the registration
    private boolean isValid() {
        flag = false;
        matcher = pattern.matcher(password);
        if (name.equals("")) {
            etName.setError("Enter your name..!");
            flag = false;
        } else if (userName.equals("")) {
            etUserName.setError("Enter a user name..!");
            flag = false;
        } else if (email.equals("")) {
            etEmail.setError("Enter your emailId..!");
            flag = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email is not valid");
            etEmail.setText("");
            flag = false;
        } else if (password.equals("")) {
            etPassword.setError("Enter a password");
            flag = false;
        } else if (!matcher.matches()) {
            etPassword.setError("Password have 5-15 characters,and it contains at least one [a-z] ,at least one [A-Z] ,at least one [0-9] and contains one special symbols @,#,$,%");
            etPassword.setText("");
            flag = false;

        } else {
            flag = true;
        }

        return flag;
    }


    //creating new user, and data to the firebase
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

                } else {
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

