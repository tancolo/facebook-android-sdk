package com.shrimpcolo.wosao;

/**
 * Created by Administrator on 2015/10/13.
 */

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static final String TAG = "Utils";
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e(TAG, "\n\n Package Name = " + context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e(TAG, "Key Hash: " + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e(TAG, "Name not found " +  e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No such an algorithm " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.toString());
        }

        return key;
    }

    /**
     * 根据路径生成bitmap
     */
    public static Bitmap decodeFile(String path) {
        if (path == null || path.isEmpty()) {
            Log.e(TAG, "path Error!!!");
            return null;
        }
        try {
            File file = new File(path);
            if (file.exists()) { // 检测文件是否存在
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, opt);
                Log.e(TAG, "decodeFile===>>path:" + path + ", " + opt.outWidth + " x " + opt.outHeight);
                opt.inJustDecodeBounds = false;
                Bitmap tmp = BitmapFactory.decodeFile(path, opt);
                // 如果图片存在,返回为null
                if (tmp == null) {
                    file.delete();
                }
                return tmp;
            }
        } catch (OutOfMemoryError err) {

            return null;
        }
        return null;
    }

    /**
     * 根据路径和参数生成bitmap
     */
    public static Bitmap decodeFile(String path, int reqWidth, int reqHeight) {
        if (path == null || path.isEmpty()) {
            Log.e(TAG, "path Error!!!");
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        opt.inSampleSize = calculateInSampleSize(opt, reqWidth, reqHeight);
        int dstWidth, dstHeight;
        if (picWidth > picHeight) {
            if (picWidth > reqWidth) {
                dstWidth = reqWidth;
                dstHeight = dstWidth * picHeight / picWidth;
            } else {
                dstWidth = picWidth;
                dstHeight = picHeight;
            }
        } else {
            if (picHeight > reqHeight) {
                dstHeight = reqHeight;
                dstWidth = dstHeight * picWidth / picHeight;
            } else {
                dstWidth = picWidth;
                dstHeight = picHeight;
            }
        }
        opt.inJustDecodeBounds = false;
        opt.outWidth = dstWidth;
        opt.outHeight = dstHeight;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
            Log.e(TAG, "decodeFile->width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
            Bitmap tmp = null;
            if (bitmap != null) {
                tmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            return tmp;
        } catch (OutOfMemoryError err) {
            Log.e(TAG, "err.toString(): " + err.toString());
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            // final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

}