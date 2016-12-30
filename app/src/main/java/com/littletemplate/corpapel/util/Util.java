package com.littletemplate.corpapel.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by ucweb02 on 30/12/2016.
 */

public class Util {

    public static void usarGlide(Context context, ImageView imageView, int ruta) {
        Glide.with(context)
                .load(ruta)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public static void usarGlide(Context context, ImageView imageView, String ruta) {
        Glide.with(context)
                .load(ruta)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }
}
