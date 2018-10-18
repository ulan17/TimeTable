package com.ulan.timetable.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ulan.timetable.R;

public class SchoolWebsiteActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_website);

        WebView webView = (WebView) findViewById(R.id.schoolwebsite);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        SharedPreferences sharedPref = getSharedPreferences("com.ulan.timetable", 0);
        String url = "https://" + sharedPref.getString("schoolwebsite", null);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(SchoolWebsiteActivity.this, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        if(TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Please, set website", Toast.LENGTH_LONG).show();
        } else {
            webView.loadUrl(url);
            setContentView(webView);
        }
    }
}
