package com.lisen.android.diskcachebitmaptest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private DiskLruCache mDiskLruCache;
    private String imgUrl = "http://tnfs.tngou.net/image/top/default.jpg_180x120";
    private ImageView imageView;
    private final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        try {
            mDiskLruCache = DiskLruCache.open(getDiskCacheDir(this, "11b"),
                    getAppVersion(this), 1, 10 * 1024 * 1024);
            String urlHash = hashKeyForDisk(imgUrl);
            Log.d(TAG, urlHash);
            LoadImage task = new LoadImage(imageView, mDiskLruCache, urlHash);
            task.execute(imgUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String hashKeyForDisk(String key) {
        String cahceKey;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cahceKey = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cahceKey = String.valueOf(key.hashCode());
        }

        return cahceKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }
}
