package com.example.shashwatsinha.imageprocessing;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by shashwatsinha on 03/08/15.
 */
public class AsyncDrawable extends ColorDrawable {
    ImageLoadTask imageLoadTask;

    AsyncDrawable(ImageLoadTask lm) {
        super(Color.BLACK);
        //super();
        imageLoadTask = lm;
    }

    ImageLoadTask getImageLoadTask() {
        return imageLoadTask;
    }
}
