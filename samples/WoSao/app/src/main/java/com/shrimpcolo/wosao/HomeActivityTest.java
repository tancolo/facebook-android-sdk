package com.shrimpcolo.wosao;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;

import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class HomeActivityTest extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "shrimpcolo";

    private ImageView imageView;

    //使用facebook自带的button登录
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils.printKeyHash(this);
        //获得callbackmanager
        callbackManager = CallbackManager.Factory.create();

        Log.e(TAG, "onCreate!!!");
        setContentView(R.layout.act_home);

//        imageView = (ImageView) findViewById(R.id.im_icon_share);
//        imageView.setOnClickListener(this);

        //获得loginButton
        loginButton = (LoginButton) findViewById(R.id.login_button);
        Log.e(TAG, "loginButton: " + loginButton);
        if (loginButton == null) {
            return;
        }
        //设置权限
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // your code, a sample as below
                Log.e(TAG, "onSuccess --------" + loginResult.getAccessToken());
                //getFacebookInfo(loginResult);
            }

            @Override
            public void onCancel() {
                // your code
                Log.e(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // your code
                Log.e(TAG, "onError: " + exception.getMessage()
                        + "\n " + exception.toString());
            }
        });
    }

    //示例 获取自己相关信息
//    private void getFacebookInfo(LoginResult loginResult) {
//        Log.e(TAG, "Token --------" + loginResult.getAccessToken().getToken());
//        Log.e(TAG, "Permision--------" + loginResult.getRecentlyGrantedPermissions());
//
//        Log.e(TAG, "OnGraph ------------------------");
//        GraphRequest request = GraphRequest.newMeRequest(
//                loginResult.getAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject object,
//                            GraphResponse response) {
//                        // Application code
//                        Log.e(TAG, "GraphResponse -------------" + response.toString());
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name,location,locale,timezone");
//        request.setParameters(parameters);
//        request.executeAsync();
//
//        Log.e(TAG, "Total Friend in List ----------------------");
//        new GraphRequest(
//                loginResult.getAccessToken(),
//                "/me/friends",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                                /* handle the result */
//                        Log.e(TAG, "Friend in List -------------" + response.toString());
//                    }
//                }
//        ).executeAsync();
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_icon_share:
                Log.e(TAG, "onClick...");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume!!!");

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    //必须实现，用于回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "request: " + requestCode + ",  resultCode: " + resultCode
                + ", data: " + data.toString());
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
