/*
 * (c) 2011 Anthony Sandrin
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */

package me.sandrin.xkcdwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GetImageTask extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
        } catch (IOException e) {
            Log.e("XKCD", "Unable to get XKCD image.", e);
        }
        return image;
    }
}
