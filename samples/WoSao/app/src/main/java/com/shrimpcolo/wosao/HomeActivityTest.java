package com.shrimpcolo.wosao;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import org.json.JSONObject;

import java.util.Arrays;

public class HomeActivityTest extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "shrimpcolo";

    //private ImageView imageView;

    //使用facebook自带的button登录
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private Utils utils = new Utils();

    //本地 Button Login
    private Button localLoginButton;

    //分享图片
    private Button shareDefaultImage;
    private Button shareSeletedImage;
    private ImageView selectedImage;
    private final static int REQUEST_CODE = 1000;
    private final static int REQUEST_CODE4_FACEBOOK = 64206;
    private String imagePath = null;
    //private Bitmap mBitmap = null;

    //ImageView 宽高值
    private int mHeight = 0;
    private int mWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils.printKeyHash(this);

        Log.e(TAG, "onCreate!!!");
        setContentView(R.layout.act_home);

//        imageView = (ImageView) findViewById(R.id.im_icon_share);
//        imageView.setOnClickListener(this);

        //获得callbackmanager
        callbackManager = CallbackManager.Factory.create();

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
                Toast.makeText(getApplicationContext(), "Login in Success!!!", Toast.LENGTH_LONG).show();
                //getFacebookInfo(loginResult);
            }

            @Override
            public void onCancel() {
                // your code
                Log.e(TAG, "onCancel");
                Toast.makeText(getApplicationContext(), "Login in Cancel!!!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // your code
                Log.e(TAG, "onError: " + exception.getMessage()
                        + "\n " + exception.toString());
                Toast.makeText(getApplicationContext(),
                        "Login in Error: " + exception.getMessage() + "\n " + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //第二种方式(LoginManager)登录facebook
        localLoginButton = (Button)findViewById(R.id.login_button_local);
        localLoginButton.setOnClickListener(this);

        //分享图片
        //1）分享默认图片
        shareDefaultImage = (Button)findViewById(R.id.bt_share1);
        shareDefaultImage.setOnClickListener(this);

        //2）分享选择图片
        shareSeletedImage = (Button)findViewById(R.id.bt_share2);
        shareSeletedImage.setOnClickListener(this);

        //选择图片
        selectedImage = (ImageView)findViewById(R.id.im_icon_share_select);
        selectedImage.setOnClickListener(this);
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
                Log.e(TAG, "onClick...share");
                break;
            case R.id.login_button_local:
                Log.e(TAG, "onClick...local button");
                localLogin();
                break;
            case R.id.bt_share1:
                //为了说明分别说明分享步骤，这里再次重复写登录步骤
                Log.e(TAG, "onClick...share default image");
                login4ShareImage();
                break;
            case R.id.bt_share2:
                Log.e(TAG, "\n onClick...share selected image to facebook");
                login4ShareSelectedImage();
                break;
            case R.id.im_icon_share_select:
                Log.e(TAG, "\n onClick...selected image");
                openFileManager();
                break;
        }
    }

    private void localLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile",
                "user_friends", "email", "user_birthday"));
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.e(TAG, "LocalLogin - onSuccess --------" + loginResult.getAccessToken());
                        Toast.makeText(getApplicationContext(), "Login in Success!!!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "LocalLogin - onCancel");
                        Toast.makeText(getApplicationContext(), "Login in Cancel!!!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "LocalLogin - onError: " + exception.getMessage()
                                + "\n " + exception.toString());

                        Toast.makeText(getApplicationContext(),
                                "Login in Error: " + exception.getMessage() + "\n " + exception.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    //图片分享
    private void login4ShareImage() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile",
                "user_friends", "email", "user_birthday"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.e(TAG, "ShareImage - onSuccess --------" + loginResult.getAccessToken());
                        Toast.makeText(getApplicationContext(), "Login in Success!!!", Toast.LENGTH_LONG).show();
                        //do share image
                        publishImage(imagePath);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "ShareImage - onCancel");
                        Toast.makeText(getApplicationContext(), "Login in Cancel!!!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "ShareImage - onError: " + exception.getMessage()
                                + "\n " + exception.toString());

                        Toast.makeText(getApplicationContext(),
                                "Login in Error: " + exception.getMessage() + "\n " + exception.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void publishImage(String imagePath) {
        //1. Bitmap
        Bitmap image = null;
        Log.e(TAG, "imagePath = " + imagePath);

        if (imagePath == null || imagePath.isEmpty()) {//默认图片分享
            //Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share );
        }else {// 分享选择图片
            image = Utils.decodeFile(imagePath);
            this.imagePath = null;
        }

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("Just for testing！！")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

        Toast.makeText(getApplicationContext(), "Shared it Success!!!", Toast.LENGTH_LONG).show();
        //image.recycle();
    }

    //选择图片分享
    private void login4ShareSelectedImage() {
        //1. 判断是否有选图片
        if (imagePath == null) {
            Toast.makeText(getApplicationContext(), "Not select image", Toast.LENGTH_SHORT).show();
            return;
        }
        //2. 登录并分享到facebook, 这里直接调用login4ShareImage， 并对publishImage函数进行修改
        login4ShareImage();
    }

    private void openFileManager() {
        Intent it = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        try {
            startActivityForResult(it, REQUEST_CODE);
        } catch (ActivityNotFoundException exp) {
            Toast.makeText(this, getResources().getString(R.string.view_no_filemanager),
                    Toast.LENGTH_SHORT);
        } catch (Exception exp) {
            Toast.makeText(this, getResources().getString(R.string.view_filemanager_error),
                    Toast.LENGTH_SHORT);
        }
    }
    private void getImageInfo(Intent data) {
        if (data == null) {
            Log.e(TAG, "data is null!");
            return;
        }
        Uri selectedUri = data.getData();
        Cursor cursor = null;
        String mimeType;

        try {
            cursor = getContentResolver().query(selectedUri, null, null, null, null);
            Log.e(TAG, "\n selectedUri = " + selectedUri + "\n cursor = " + cursor
                    + "\n started string = "
                    + selectedUri.toString().substring(0, 7));

            if (selectedUri != null
                    && selectedUri.toString().substring(0, 10)
                    .equals("content://") && cursor != null
                    && cursor.moveToFirst()) {

                mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                Log.e(TAG, "mimeType  = " + mimeType);

                imagePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Log.e(TAG, "imagePath = " + imagePath);
            }

            // ImageView显示图片
            //1. 为了UI效果的美观, 2等分布局, 需要获得imageview 大小, 然后将选择的图片缩放为imageview大小
            //注意：在onCreate中使用getHeight & getWidth获得控件的宽高是不行的，这是因为他们自己还没有被度量好。
            Bitmap bitmap = null;
            mHeight = selectedImage.getHeight();
            mWidth = selectedImage.getWidth();
            Log.e(TAG, "width = " + mWidth + ", mHeight = " + mHeight);

            //2. 将图片压缩成ImageView大小的bitmap
            if (imagePath != null && !imagePath.isEmpty()) {
                bitmap = Utils.decodeFile(imagePath, mWidth, mHeight);
                selectedImage.setImageBitmap(bitmap);
            }

        }catch (Exception exp) {
            exp.printStackTrace();
            return;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume!!!");

        // Logs 'install' and 'app activate' App Events.
        //AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        //AppEventsLogger.deactivateApp(this);
    }

    //必须实现，用于回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "request: " + requestCode + ",  resultCode: " + resultCode
                + ", data: " + ((data != null) ? data.toString() : "null")  );

        //facebook request code
        if (requestCode == REQUEST_CODE4_FACEBOOK && resultCode == RESULT_OK ) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {//获得选中的图片Uri
            Log.e(TAG, "onActivityResult(): data = " + data);
            getImageInfo(data);
        }
    }
}
