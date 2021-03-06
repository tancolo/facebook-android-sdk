## WoSao
展示如何集成Sdk到自有apk中。

##步骤
* 生成debug & release Key Hash
* 集成FB Sdk
* 实现FB 2种登录方式
* 分享本地图片
* 多途径分享图片

### 生成debug & release Key Hash
在实际测试过程中，发现使用debug版本的 Key Hash会概率性出现问题，提示说需要在facebook开发网站上设置当前app_id对应的Key Hash。
这里总结生成debug key 以及 release key方法。

#### 命令行生成
- **Debug Key Hash**
```
C:\Users\Administrator>keytool -exportcert  -keystore C:\Users\Administrator\.android\debug.keystore  | openssl sha1 -binary | openssl base64
输入密钥库口令:  android
bxxxxxxNM5QmqJ8WcFpQR6cys40=
```
口令默认是 android, 生成Key Hash是28位字符!

- **Release Key Hash**
```
C:\Users\Administrator>keytool -exportcert  -keystore C:\Users\Administrator\.android\.keystore\release-key.jks  | openssl sha1 -binary | openssl base64
输入密钥库口令:  Your_Password
bxxxxxx....xxxx=
```
口令是自己App release key 密码, 生成Key Hash是28位字符!

#### 代码生成
**推荐使用代码生成方法**
详细的参看
```
@Utils.java
....
Log.e(TAG, "Key Hash: " + key);

```

### 集成FB Sdk
- 创建Android Studio 工程， 按照facebook要求，Minimum SDK 需要在15以上(含).
``` Select API 15: Android 4.0.3 or higher and create your new project. ```

- 打开 your_app | build.gradle 文件,在其中添加
```
//step_2 添加maven仓库
repositories{
    mavenCentral()
}//end

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.2.1'

    //step_3 facebook sdk version
    compile 'com.facebook.android:facebook-android-sdk:4.8.0'
}
```

- 将从facebook开发者网站获取的该App的Id 存放在string.xml中。不知道如何获取，参考
[Android Studio集成Facebook SDK](http://blog.csdn.net/shrimpcolo/article/details/49153289)

```
<!-- step_01 facebook app_id -->
    <string name="facebook_app_id">1640967989507233</string>
```
- 以上完毕后，**需要编译工程**，gradle自动下载facebook sdk 4.8.0版本。
**注意**：本地测试 发现依赖4.8.0版本会出现错误, 改为4.6.0版本依赖！
```
12-10 21:56:01.570 E/AndroidRuntime(12307): FATAL EXCEPTION: main
12-10 21:56:01.570 E/AndroidRuntime(12307): Process: com.shrimpcolo.wosao, PID: 12307
12-10 21:56:01.570 E/AndroidRuntime(12307): java.lang.NullPointerException
12-10 21:56:01.570 E/AndroidRuntime(12307):     at com.facebook.FacebookButtonBase.getFragment(FacebookButtonBase.java:105)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at com.facebook.login.widget.LoginButton$LoginClickListener.onClick(LoginButton.ja
va:736)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at com.facebook.FacebookButtonBase$1.onClick(FacebookButtonBase.java:383)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.view.View.performClick(View.java:4446)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.view.View$PerformClick.run(View.java:18485)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.os.Handler.handleCallback(Handler.java:733)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.os.Handler.dispatchMessage(Handler.java:95)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.os.Looper.loop(Looper.java:136)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at android.app.ActivityThread.main(ActivityThread.java:5290)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at java.lang.reflect.Method.invokeNative(Native Method)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at java.lang.reflect.Method.invoke(Method.java:515)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:859)

12-10 21:56:01.570 E/AndroidRuntime(12307):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:675)
12-10 21:56:01.570 E/AndroidRuntime(12307):     at dalvik.system.NativeStart.main(Native Method)
12-10 21:56:17.270 I/Process (12307): Sending signal. PID: 12307 SIG: 9
12-10 21:56:17.290 I/ActivityManager(  660): Process com.shrimpcolo.wosao (pid 12307) has died.
```

- 要使facebook sdk能正常使用，还需要使用网络权限，以及mete-data声明
```
<!--step_4 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- end step_4 -->
```

```
<application android:label="@string/app_name" ...>
    ...
    <meta-data android:
    name="com.facebook.sdk.ApplicationId"
    android:value="@string/facebook_app_id"/>
    ...
</application>
```

- 使用Login 和 Share功能
为使用facebook的Login 或者 Share，还需要增加FacebookActivity到manifest文件:
```
<activity android:name="com.facebook.FacebookActivity"
            android:configChanges=
                "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:label="@string/app_name" />
```

- 发送图片或视频
如果你需要分享 链接， 图片， 视频， 那么你同样需要声明FacebookContentProvider 到manifest中。
这个在 **分享本地图片** 那里说明。

### 实现FB 2 种登录方式
Facebook SDK 提供2中方法供你登录Facebook：
- LoginButton  class - 提供登录button， 你可以添加到自己的UI中，使用当前access token 并且可以登录，登出！
- LoginManager class - 不使用UI 元素来初始化登录！

#### LoginButton实现登录
- 首先在act_layout中添加自带的button 元素。
    ```
    <LinearLayout
            android:id="@+id/ll_social_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_above="@+id/rl_home_bottom_container"
            android:layout_marginBottom="30dp">

            <com.facebook.login.widget.LoginButton
                android:id="@+id/login_button"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_horizontal" />
    ```
    其中com.facebook.login.widget.LoginButton就是facebook提供的UI控件， 可以添加自己的android属性。

- 其次需要在HomeActivityTest.java中实现登录步骤

    1.**在Applicaion中初始化facebook sdk， 官方推荐的方法。**
    ```
    public void onCreate() {
            super.onCreate();
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
    ```
    不要忘记在AndroidManifest.xml中 application标签下添加 ``` android:name=".MyApplication" ```

    2.**定义LoginButton & CallbackManager对象**
    ```
        private LoginButton loginButton;
        private CallbackManager callbackManager;
    ```
    3.**在onCreate中实例化对象**
    ```
     protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ...
            //获得callbackmanager
            callbackManager = CallbackManager.Factory.create();
            setContentView(R.layout.act_home);
            ... ...
            //获得loginButton
            loginButton = (LoginButton) findViewById(R.id.login_button);
            if (loginButton == null) {
                  return;
            }
        ... ...
     }
    ```
    4.**设置读取权限以及注册回调**
    ```
    loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
               Log.e(TAG, "onSuccess --------");
          }

          @Override
          public void onCancel() {
              Log.e(TAG, "onCancel");
          }

          @Override
          public void onError(FacebookException exception) {
              Log.e(TAG, "onError: " + exception.getMessage() + "\n " + exception.toString());
          }
    });
    ```
    5.**重写onActivityResult**
    ```
    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            Log.e(TAG, "request: " + requestCode + ",  resultCode: " + resultCode
                    + ", data: " + data.toString());
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    ```

#### LoginManager实现登录
为实现本地Button登录facebook, 这里仅仅添加了几个步骤。
- 首先在act_layout中添加本地Button 元素。
    ```
                <Button
                android:id="@+id/login_button_local"
                android:layout_width="wrap_content"
                android:layout_height="40dp"
                android:text="@string/local_login_button"
                android:layout_gravity="center_horizontal"
                />
    ```

- 其次,在HomeActivityTest.java中添加
    ```
    //本地 Button Login
    private Button localLoginButton;
    ```

    在onCreate()
    ```
            //第二种方式(LoginManager)登录facebook
            localLoginButton = (Button)findViewById(R.id.login_button_local);
            localLoginButton.setOnClickListener(this);
    ```

- 最后，在onClick()中调用函数localLogin 实现登录以及回调处理
    ```
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

    ```
    记得onActivityResult重写是必须的！

### 分享本地图片
这里设置2个目标：1. 分享默认图片； 2. 选择图片分享。

#### 分享默认图片
- 首先，在act_home中添加xml 元素
    ```
        <!-- 分享图片-->
        <LinearLayout
            android:id="@+id/ll_share_container"
            android:layout_width="match_parent"
            android:layout_height="300dp"
            android:orientation="horizontal"
            android:layout_above="@+id/ll_social_container"
            android:layout_marginBottom="30dp">

            <LinearLayout
                android:id="@+id/ll_share1_container"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:orientation="vertical"
                android:layout_weight="1">

                <ImageView
                    android:id="@+id/im_icon_share_default"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:scaleType="fitXY"
                    android:layout_weight="5"
                    android:src="@drawable/icon_share"
                    />
                <Button
                    android:id="@+id/bt_share1"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/button_share_txt"
                    android:layout_gravity="center_horizontal"
                    android:layout_weight="1"
                    />

            </LinearLayout>
            <LinearLayout
                android:id="@+id/ll_share2_container"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:orientation="vertical"
                android:layout_weight="1">

                <ImageView
                    android:id="@+id/im_icon_share_select"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:scaleType="fitXY"
                    android:layout_weight="5"
                    android:text="@string/iv_select_file"
                    />
                <Button
                    android:id="@+id/bt_share2"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/button_share_txt"
                    android:layout_gravity="center_horizontal"
                    android:layout_weight="1"
                    />

            </LinearLayout>

        </LinearLayout>
    ```
    注意，这里添加了多个布局文件，为了后续选择图片分享的实现。

- 其次， 在代码中实现
    ```
        //分享图片
        private Button shareDefaultImage;
    ```

    ```
            //1）分享默认图片
            shareDefaultImage = (Button)findViewById(R.id.bt_share1);
            shareDefaultImage.setOnClickListener(this);
    ```

    ```
                case R.id.bt_share1:
                    //为了说明分别说明分享步骤，这里再次重复写登录步骤
                    Log.e(TAG, "onClick...share default image");
                    login4ShareImage();
                    break;
    ```

- 最后, 在login4ShareImage中重复写了登录facebook的步骤， 分享的关键代码段如下:
    ```
        private void publishImage() {
            //Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share );
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .setCaption("Just for testing！！")
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareApi.share(content, null);
        }
    ```

#### 选择图片分享
这个主题需要添加的内容比较多：
- 选择图片
- 生成特定宽高bitmap

##### 选择图片
- 打开文件管理器
    ```
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
    ```
- 回调处理
需要在onActivityResult中处理

    ```
    else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {//获得选中的图片Uri
                Log.e(TAG, "onActivityResult(): data = " + data);
                getImageInfo(data);
            }
    ```
- 获取必要信息
    ```
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
                    bitmap.recycle();
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
    ```


##### 生成特定宽高bitmap
这里用到了Utils中的2个函数，通过文件路径获得bitmap。
- **1. decodeFile(String path)**
- **2. decodeFile(String path, int reqWidth, int reqHeight)**
详细的 参看源码!
在Wosao中 图片展示使用 [2] 个， 发送给facebook使用 [1] 函数。
