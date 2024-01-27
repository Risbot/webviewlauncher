package com.uarmena.webviewlauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String preference = "launch";
    public static final String urlKey = "url";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        sharedpreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
        WebView wv = findViewById(R.id.wv);
        Button submit = findViewById(R.id.submit);
        EditText urlInput = findViewById(R.id.url);
        LinearLayout layout = findViewById(R.id.layout);


        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.setWebViewClient(new SSLTolerentWebViewClient());

        if (sharedpreferences.contains(urlKey)) {
            layout.setVisibility(View.GONE);
            wv.setVisibility(View.VISIBLE);
            wv.loadUrl(sharedpreferences.getString(urlKey, ""));
            NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
            connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final WebView webView = findViewById(R.id.wv);
                            webView.reload();
                        }
                    });

                }
            });
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(urlKey, urlInput.getText().toString());
                editor.commit();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
    }
}