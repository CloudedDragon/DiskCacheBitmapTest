package com.lisen.android.diskcachebitmaptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/5/31.
 */
public class LoadImage extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "LoadImage";
    private ImageView imageView;
    private DiskLruCache mDiskCache;
    private String urlCode;
    public LoadImage(ImageView view, DiskLruCache diskLruCache, String imgurl) {
        imageView = view;
        mDiskCache = diskLruCache;
        urlCode = imgurl;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        HttpURLConnection connection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8 * 1000);
            connection.setReadTimeout(8 * 1000);
            InputStream is = connection.getInputStream();
            Bitmap bitmap = null;
            if (is != null) {
                bitmap = BitmapFactory.decodeStream(is);

                DiskLruCache.Editor editor = mDiskCache.edit(urlCode);
                OutputStream outputStream = editor.newOutputStream(0);
                int i = 0;
                in = new BufferedInputStream(is);
                out = new BufferedOutputStream(outputStream);
                int b;
                while ((b = in.read()) != -1) {
                    if (i < 10) {
                        Log.d(TAG, i + "");
                        i++;
                    }
                    out.write(b);
                }
                editor.commit();



            }
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "地址有错");

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (imageView != null && bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
