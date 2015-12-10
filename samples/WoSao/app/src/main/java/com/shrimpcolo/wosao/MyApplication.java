package com.shrimpcolo.wosao;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;

/**
 * Created by Administrator on 2015/12/10.
 */
public class MyApplication extends Application {
    private static final String TAG = "shrimpcolo";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Application onCreate()");
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
