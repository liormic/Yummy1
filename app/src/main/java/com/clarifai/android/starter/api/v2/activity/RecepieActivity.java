package com.clarifai.android.starter.api.v2.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clarifai.android.starter.api.v2.R;

public class RecepieActivity extends AppCompatActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        getWebView();







    }



    public void getWebView(){

        String id = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("ProductName");
        }
   
        WebView myWebView;
     //   myWebView.setWebViewClient(new WebViewClient());

        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("http://www.epicurious.com/search/"+id+"?content=recipe");
    }

  //  @Override
   // protected void onPause() {
    //    super.onPause();
    //    BaseActivity.setSearchTerm("");
    //}

}
