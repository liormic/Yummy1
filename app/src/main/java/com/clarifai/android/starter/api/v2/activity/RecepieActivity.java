package com.clarifai.android.starter.api.v2.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clarifai.android.starter.api.v2.R;

public class RecepieActivity extends AppCompatActivity {


    private WebView myWebView;
    private static String id = null;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("ProductName");
        }
        currentUrl = "http://www.epicurious.com/search/" + id + "?content=recipe";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BOOKMARKS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(currentUrl);
        getWebView();
    }


    public void getWebView() {


        //   myWebView.setWebViewClient(new WebViewClient());


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                currentUrl = url;

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                myWebView.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    myWebView.loadUrl(request.getUrl().toString());
                }
                return true;
            }

        });


    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }

        //  @Override
        // protected void onPause() {
        //    super.onPause();
        //    BaseActivity.setSearchTerm("");
        //}

    }
}
