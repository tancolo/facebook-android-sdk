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

