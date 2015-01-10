/*
 * (c) 2011 Anthony Sandrin
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */

package me.sandrin.xkcdwidget;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetJsonTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        String jsonText = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            jsonText = reader.readLine();
        } catch (IOException e) {
            Log.e("XKCD", "Unable to get XKCD Json.", e);
        }

        return jsonText;
    }
}
