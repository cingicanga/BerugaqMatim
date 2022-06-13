package com.android.berugaqmatim;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.Uri;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.widget.Toast;
import java.util.Objects;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    private String TAG;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //tampilkan splashscreen
        setTheme(R.style.Theme_BerugaqMatim);
        // sembunyikan title dan buat fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://pajakmatim.info");
        webView.setWebViewClient(new mywebclient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
    }
    //loading gak tuh, kok gak muncul ya wtf, ini doang yang belum berhasil
    private ProgressBar spinner;
    // panggil splash screen sambil menunggu load, page ini berhasil
    // (sembunyikan kalau sudah selesai load page), ini juga berhasil
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            webview.setVisibility(webView.INVISIBLE);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            spinner.setVisibility(View.GONE);
            view.setVisibility(webView.VISIBLE);
            super.onPageFinished(view, url);
        }
    }
    //mengatasi error url whatsapp, telepon, telegram
    public class mywebclient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url){
            if (url.startsWith("tel:") || url.startsWith("whatsapp:") || url.startsWith("mailto:") || url.startsWith("tg:") || url.contains("facebook") || url.contains("twitter") || url.contains("youtube")  || url.contains("instagram")  || url.contains("goo.gl/maps")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return false;
            }
    }

    //Memunculkan notifikasi ketika tombol back ditekan dan pilihan keluar aplikasi
    boolean doubleBackToExitPressedOnce;
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (doubleBackToExitPressedOnce) {
            Log.i(TAG, "double click");
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Berugaq Matim")
                    .setMessage("apakah anda ingin keluar dari aplikasi?")
                    .setPositiveButton("Keluar",
                            (dialog, which) -> finish()).setNegativeButton("Tidak", null).show();
            return;
        } else {
            Log.i(TAG, "single click");
            if (webView.canGoBack()) {
                Log.i(TAG, "canGoBack");
                webView.goBack();
            } else {
                Log.i(TAG, "nothing to canGoBack");
            }
        }
        this.doubleBackToExitPressedOnce = true;
        if (getApplicationContext() == null) {
            return;
        } else {
            Toast.makeText(this, "Tekan tombol kembali sekali lagi untuk keluar dari aplikasi",
                    Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
    // mengatasi SSL certificate errors (jika ada)
    static class SSLTolerantWebViewClient extends WebViewClient {
        @SuppressLint("WebViewClientOnReceivedSslError")
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (error.toString().equals("piglet"))
                handler.cancel();
            else
                handler.proceed();
        }
    }
}
