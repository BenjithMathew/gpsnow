package com.surroundsync.gpsnow.facebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.surroundsync.gpsnow.R;

import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

public class FacebookIntegration extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_facebook_integration);
        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (LoginButton)findViewById(R.id.activity_facebook_login_button);
        fbLoginButton.setReadPermissions("email");

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                Bundle params = new Bundle();
                params.putString("fields", "id,email,name,picture");



                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            String userId = loginResult.getAccessToken().getUserId();
                            String userName=jsonObject.getString("name").toString();
                            // JSONObject data = graphResponse.getJSONObject();
                            //info.setText(jsonObject.getString("name"));
                            //tvEmail.setText(jsonObject.getString("email"));
                            //profilePictureView.setProfileId(userId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

                request.setParameters(params);
                request.executeAsync();
                Toast.makeText(getBaseContext(),"Login Successful", LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(),"Login Failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getBaseContext(),"Error...!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }






}
