# WoSao
展示如何集成Sdk到自有apk中。

#步骤
* 集成FB Sdk
* 实现FB 2种登录方式
* 分享本地图片
* 多途径分享图片

## 集成FB Sdk
1. 创建Android Studio 工程， 按照facebook要求，Minimum SDK 需要在15以上(含).

``` Select API 15: Android 4.0.3 or higher and create your new project. ```

2. 打开 your_app | build.gradle 文件,在其中添加
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

3. 将从facebook开发者网站获取的该App的Id 存放在string.xml中。不知道如何获取，参考
[Android Studio集成Facebook SDK](http://blog.csdn.net/shrimpcolo/article/details/49153289)

```
<!-- step_01 facebook app_id -->
    <string name="facebook_app_id">1640967989507233</string>
```

4. 以上完毕后，**需要编译工程**，gradle自动下载facebook sdk 4.8.0版本。

5. 要使facebook sdk能正常使用，还需要使用网络权限，以及mete-data声明
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

6.使用Login 和 Share功能
为使用facebook的Login 或者 Share，还需要增加FacebookActivity到manifest文件:
```
<activity android:name="com.facebook.FacebookActivity"
            android:configChanges=
                "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:label="@string/app_name" />
```

7. 发送图片或视频
如果你需要分享 链接， 图片， 视频， 那么你同样需要声明FacebookContentProvider 到manifest中。
这个在 **分享本地图片** 那里说明。

