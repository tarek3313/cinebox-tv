package com.cinebox.tv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;
    // প্রয়োজন অনুযায়ী আপনার ওয়েবসাইট বা পোর্টালের URL দিন
    private static final String START_URL = "https://example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUi();
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.tvWebView);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("vlc://") || url.endsWith(".m3u8") || url.endsWith(".mp4") || url.endsWith(".mkv"))) {
                    openInVlc(url);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(START_URL);
    }

    private void openInVlc(String mediaUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("org.videolan.vlc");
            intent.setDataAndType(Uri.parse(mediaUrl), "video/*");
            intent.putExtra("from_start", true);
            startActivity(intent);
        } catch (Exception e) {
            Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(mediaUrl));
            startActivity(fallback);
        }
    }

    private void hideSystemUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }
                       }
