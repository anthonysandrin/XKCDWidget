/*
 * (c) 2011 Anthony Sandrin
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */

package me.sandrin.xkcdwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class XKCDAppWidgetProvider extends AppWidgetProvider {
    private static final String UPDATE = "XKCDAppWidgetProvider.UPDATE";

    private String title;
    private String altText;
    private Bitmap image;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        title = "";
        altText = "";
        image = null;

        updateComicInfo();

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
            updateRemoteViews(context, views);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (UPDATE.equals(intent.getAction())) {
            update(context);
        }
    }

    private void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
        ComponentName watchWidget = new ComponentName(context, XKCDAppWidgetProvider.class);

        title = context.getResources().getString(R.string.error_title);
        image = null;
        altText = context.getResources().getString(R.string.error_text);
        updateComicInfo();
        updateRemoteViews(context, remoteViews);

        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    private void updateComicInfo() {
        GetJsonTask jsonTask = new GetJsonTask();
        jsonTask.execute("http://xkcd.com/info.0.json");
        String jsonText = null;
        try {
            jsonText = jsonTask.get();
        } catch (InterruptedException e) {
            Log.e("XKCD", "Unable to get XKCD Json.", e);
        } catch (ExecutionException e) {
            Log.e("XKCD", "Unable to get XKCD Json.", e);
        }

        String imageUrl = null;
        if(jsonText != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonText);
                title = jsonObject.getString("title");
                imageUrl = jsonObject.getString("img");
                altText = jsonObject.getString("alt");
            } catch (JSONException e) {
                Log.e("XKCD", "Unable to parse XKCD Json.", e);
            }
        }

        if(imageUrl != null) {
            GetImageTask imageTask = new GetImageTask();
            try {
                imageTask.execute(imageUrl);
                image = imageTask.get();
            } catch (InterruptedException e) {
                Log.e("XKCD", "Unable to get XKCD image.", e);
            } catch (ExecutionException e) {
                Log.e("XKCD", "Unable to get XKCD image.", e);
            }
        }
    }

    private void updateRemoteViews(Context context, RemoteViews views) {
        if(title != null) {
            views.setTextViewText(R.id.title, title);
        }

        if(image != null) {
            views.setImageViewBitmap(R.id.image, image);
        } else {
            views.setImageViewBitmap(R.id.image, null);
        }

        Intent goToSiteIntent = new Intent(Intent.ACTION_VIEW);
        goToSiteIntent.setData(Uri.parse("http://xkcd.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, goToSiteIntent, 0);
        views.setOnClickPendingIntent(R.id.image, pendingIntent);

        if(altText != null) {
            views.setTextViewText(R.id.alt_text, altText);
        }

        Intent intent = new Intent(context, getClass());
        intent.setAction(UPDATE);
        views.setOnClickPendingIntent(R.id.sync, PendingIntent.getBroadcast(context, 0, intent, 0));
    }
}
