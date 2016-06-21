package com.surroundsync.gpsnow.startapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.surroundsync.gpsnow.R;
import com.surroundsync.gpsnow.login_Map.LoginActivity;

/**
 * Created by Ashray Joshi on 21-Jun-16.
 */
public class StartActivity extends Activity implements View.OnClickListener{
    Button btnLogin;
    private LoginButton btnFbLogin;
    private SignInButton btnGLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_start);
        btnFbLogin= (LoginButton) findViewById(R.id.activity_fb_login_button);
        btnGLogin= (SignInButton) findViewById(R.id.activity_googleplus_btn_signin);
        btnLogin= (Button) findViewById(R.id.activity_start_btn_login);
        //btnGLogin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_start_btn_login:Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            break;

        }
    }
}
