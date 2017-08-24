package acondesa.acondesaapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import static android.R.attr.targetPackage;

public class ContactActivity extends AppCompatActivity {

    private WebView webview4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        webview4 = (WebView) findViewById(R.id.webview4);
        WebSettings webSettings = webview4.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setLoadsImagesAutomatically(true);



        webview4.loadUrl(getString(R.string.contactus_url));
        webview4.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(url.startsWith("tel:")){
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                }else if(url.startsWith("mailto:")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }


                if (Uri.parse(url).getHost().equals("acondesa.com.co")) {
                    // This is my web site, so do not override; let my WebView load the page
                    // Toast.makeText(getContext(), "Sigo dentro de acondesa", Toast.LENGTH_LONG).show();
                    return false;//aqui debe ser false,
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

                    Toast.makeText(getApplicationContext(), "saliendo de acondesaApp", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/conection_error.html");
            }

        });
    }

    private boolean isAppInstalled(String targetPackage){
        Context c = getApplicationContext();
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage,
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }


}
