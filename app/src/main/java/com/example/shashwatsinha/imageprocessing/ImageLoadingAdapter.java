package com.example.shashwatsinha.imageprocessing;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

/**
 * Created by shashwatsinha on 02/08/15.
 */
public class ImageLoadingAdapter implements ListAdapter {

    private Context mContext;
    private Integer[] drawables;
    LayoutInflater inflater;
    LruCache<String, Bitmap> bitmapLruCache;
    DiskLruCache mDiskLruCache;

    public ImageLoadingAdapter(Integer[] images, Context c, LruCache<String, Bitmap> cache, DiskLruCache diskLruCache) {
        mContext = c;
        drawables = images;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bitmapLruCache = cache;
        mDiskLruCache = diskLruCache;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return drawables.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (View) inflater.inflate(R.layout.image_layout, parent, false);
        } else {
            ImageView view = (ImageView) convertView.findViewById(R.id.image_view);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.image_view);

        String imageKey = String.valueOf(drawables[position % drawables.length]);

        Bitmap b = bitmapLruCache.get(imageKey);


        if (b != null) {
            iv.setImageBitmap(b);
            Log.d("shashwat", "getting in cache and returning " + position);
            return convertView;
        }

        if (cancelAsyncTask(iv, position % drawables.length)) {

            Log.d("shashwat", "ImageLoading call going for " + position);
            ImageLoadTask im = new ImageLoadTask(iv, mContext, bitmapLruCache, drawables[position % drawables.length], position,mDiskLruCache );
            iv.setImageDrawable(new AsyncDrawable(im));
            im.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, drawables[position % drawables.length]);
        }
        return convertView;


    }

    boolean cancelAsyncTask(ImageView iv, int position) {
        Drawable test = iv.getDrawable();
        if (test != null && test instanceof AsyncDrawable) {
            AsyncDrawable asyncDrawable = (AsyncDrawable) iv.getDrawable();
            if (asyncDrawable != null) {
                ImageLoadTask im = asyncDrawable.getImageLoadTask();
                if (im != null) {
                    if (im.getDrawable() != position) {
                        im.cancel(true);
                        return true;
                    } else
                        return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
