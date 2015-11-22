package com.example.shashwatsinha.imageprocessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

/**
 * Created by shashwatsinha on 01/08/15.
 */
public class MyActivity extends Activity {

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;
    private int mDiskCacheSize = 10 * 1024 * 1024; //Disk Cache Size
    private String mDiskPathSystem = "thumbnails";
    private Object mDiskCacheLock = new Object();

    private Integer[] drawables = {
            //R.drawable.zero,
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four,
            R.drawable.five,
            R.drawable.six,
            R.drawable.seven,
            R.drawable.eight,
            R.drawable.nine,
            R.drawable.ten,
            R.drawable.eleven,
            R.drawable.twelve,
            R.drawable.thirteen,
            R.drawable.fourteen,
            R.drawable.fifteen,
            R.drawable.sixteen,
            R.drawable.seventeen,
            R.drawable.eighteen,
            R.drawable.nighteen,
            R.drawable.tewenty
    };

    class InitDiskCacheLock extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {

            synchronized (mDiskCacheLock) {
                File f = params[0];
                try {
                    mDiskCache = DiskLruCache.open(f, 1, 1, mDiskCacheSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

    }


    @Override
    public void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView iv = (ImageView) findViewById(R.id.image_view);
//        new ImageLoadTask(iv, getApplicationContext()).execute(R.drawable.bg_overlay_pg);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // maximum heap size in KB
        final int cacheSize = (int) maxMemory / 8;  //cache size is 1/8 of total memory size
        Log.d("shashwat", "CacheSize is " + cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(8) {
            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                Log.d("shashwat", "I am getting evicted here evicted" + evicted);
                Log.d("shashwat", "I am getting evicted here key " + key);
                Log.d("shashwat", "I am getting evicted here oldValue " + oldValue);
                Log.d("shashwat", "I am getting evicted here newValue " + newValue);
                Constants.resuableImagesList.add(oldValue);
            }
        };
        File mDiskCacheLocation = getDiskCacheLocation(mDiskPathSystem);

        //`df  new InitDiskCacheLock().execute(mDiskCacheLocation);


        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(new ImageLoadingAdapter(drawables, getApplicationContext(), mMemoryCache, mDiskCache));


    }

    File getDiskCacheLocation(String mDiskPathSystem) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + mDiskPathSystem);
        Log.d("shashwat", "file path " + Environment.getExternalStorageDirectory().toString());
        return file;
    }
}