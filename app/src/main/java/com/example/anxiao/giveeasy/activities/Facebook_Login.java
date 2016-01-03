package com.example.anxiao.giveeasy.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.anxiao.giveeasy.BuildConfig;
import com.example.anxiao.giveeasy.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;


public class Facebook_Login extends FragmentActivity {

    private CallbackManager callbackManager;
    String facebookId;
    String fullName;
    private ShareDialog shareDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        updateWithToken(AccessToken.getCurrentAccessToken());




        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {


                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        //save profile ID
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            facebookId = profile.getId();
                            fullName = profile.getName();

                        }


                        // Send a graph request, as sometimes the profile returned is null, even though the login was ok'
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.d("app", "Login_Activity.FacebookCallback.onSuccess.onCompleted entered");

                                        //save profile ID again, as sometimes there is a problem with the original request
                                        Profile profile = Profile.getCurrentProfile();
                                        if (profile != null) {
                                            facebookId = profile.getId();
                                            fullName = profile.getName();


                                            if (BuildConfig.DEBUG) {
                                                Log.d("app", "Login_Activity.FacebookCallback.onSuccess.onCompleted BuildConfig.DEBUG==true");
                                                FacebookSdk.setIsDebugEnabled(true);
                                            }

                                            //save profile ID
                                            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
                                            System.out.println("AccessToken.getCurrentAccessToken()" + AccessToken
                                                    .getCurrentAccessToken()
                                                    .toString());

                                        }
                                    }
                                });
                        request.executeAsync();
                        updateWithToken(AccessToken.getCurrentAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(Facebook_Login.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }

                });//login manager

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);



        setContentView(R.layout.facebook_login);
    }


    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(Facebook_Login.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };




    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(Facebook_Login.this, MainActivity.class);
                    startActivity(i);

                    finish();
                }
            }, 1000);
        }
    }


}
