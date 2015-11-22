package com.example.shashwatsinha.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.util.Iterator;

/**
 * Created by shashwatsinha on 02/08/15.
 */
public class ImageLoadTask extends AsyncTask<Integer, Void, Bitmap> {

    private ImageView imageView;
    private Context mContext;
    private Integer drawable = -1;
    private LruCache<String, Bitmap> mLruCache;
    private DiskLruCache mDiskCache;
    Integer position;
    int position2;


    public ImageLoadTask(View imageView, Context mContext, LruCache<String, Bitmap> lruCache, Integer i, int position2, DiskLruCache mDiskCache) {
        this.imageView = (ImageView) imageView;
        this.mContext = mContext;
        mLruCache = lruCache;
        position = i;
        this.position2 = position2;
        this.mDiskCache = mDiskCache;

    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
//        try {
//            Thread.currentThread().sleep(5000);
//        } catch (InterruptedException ie) {
//
//        }
        drawable = params[0];
//        try {
//
//           // mDiskCache.get(String.valueOf(params[0]));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Bitmap b = downloadBitmap(params[0]);

        mLruCache.put(String.valueOf(params[0]), b);
        // mDiskCache.put(String.valueOf(params[0]), b);


        return b;


    }

    public Integer getDrawable() {
        return drawable;
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        if (isCancelled()) {
            b = null;
            return;
        }
        if (imageView != null && b != null) {
            if (isCancelled()) {
                b = null;
                return;
            }
            imageView.setImageBitmap(b);
            Log.d("shashwat", "getting from cache before putting it  " + mLruCache.get(String.valueOf(position)));
            Log.d("shashwat", "putting in cache " + position2);


        }
    }

    private Bitmap downloadBitmap(Integer param) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), param, options);
        int width = options.outWidth;
        int height = options.outHeight;
        Log.d("shashwat", "Image Dimensions are Height  " + height + " Width " + width);
        int reqWidth = (int) mContext.getResources().getDimension(R.dimen.image_view_width);
        int reqHeight = (int) mContext.getResources().getDimension(R.dimen.image_view_height);
        int scale = imageScaleFactor(width, height, reqWidth, reqHeight);
        Log.d("shashwat", "But Reguired  dimensions are Height  " + reqHeight + " Width " + reqWidth + "scaleing are " + scale);
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Iterator<Bitmap> ie = Constants.resuableImagesList.iterator();
        if (ie.hasNext()) {
            Bitmap b = ie.next();
            ie.remove();
            options.inBitmap = b;
            options.inMutable = true;
        }

        return BitmapFactory.decodeResource(mContext.getResources(), param, options);
    }

    private int imageScaleFactor(int width, int height, int reqWidth, int reqHeight) {
        int scale = 1;
        while (((width / scale) > reqWidth) && ((height / scale) > reqHeight)) {
            scale *= 2;
        }
        return scale / 2;
    }
}
