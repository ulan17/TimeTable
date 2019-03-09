package com.ulan.timetable.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

/**
 * Created by Ulan on 19.10.2018.
 */
public class BrowserUtil {
    public static void openUrlInChromeCustomTab(Context context, String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(context, url.contains("http://") ? Uri.parse(url) : Uri.parse("http://" + url));
        } catch (ActivityNotFoundException e) {
            // might not available.
            //openUrlLinkInWebView(context, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
