package com.shrimpcolo.wosao;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;


public class HomeActivityTest extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "shrimpcolo";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate!!!");
        setContentView(R.layout.act_home);

        imageView = (ImageView) findViewById(R.id.im_icon_share);
        imageView.setOnClickListener(this);
    }

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
    }
}
