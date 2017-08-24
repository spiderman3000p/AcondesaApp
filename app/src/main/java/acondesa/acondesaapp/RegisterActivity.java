package acondesa.acondesaapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private WebView webview3;
    private ProgressBar bar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bar2 = (ProgressBar) findViewById(R.id.progressbarregister);
        webview3 = (WebView) findViewById(R.id.webview3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webview3.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webview3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings webSettings = webview3.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);

        String urlToLoad = getString(R.string.app_register_url);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);


        webview3.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webview, String url, Bitmap favicon) {
                super.onPageStarted(webview, url, favicon);
                bar2.setVisibility(View.VISIBLE);
                webview3.setVisibility(View.GONE);
            }



            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                bar2.setVisibility(View.GONE);
                webview3.setVisibility(View.VISIBLE);
                //swipe.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if (Uri.parse(url).getHost().equals("acondesa.com.co")) {
                    // This is my web site, so do not override; let my WebView load the page
                    // Toast.makeText(getContext(), "Sigo dentro de acondesa", Toast.LENGTH_LONG).show();
                    return false;
                    // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                }
                    Toast.makeText(RegisterActivity.this, "saliendo de acondesaApp url:" + url, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/conection_error.html");
            }

        });

        webview3.loadUrl(urlToLoad);

    }
}
