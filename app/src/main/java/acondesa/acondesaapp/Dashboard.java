package acondesa.acondesaapp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static String username;
    private static String password;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "MyPrefs";
    private static Boolean logged_in = false;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onResume() {
        super.onResume();
        getIntent().getExtras();
        Context context = Dashboard.this;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "none");
        password = sharedpreferences.getString("password", "none");
        if (password.equals("none") && username.equals("none") && !logged_in) {
            //enviar al login
            Intent intent = new Intent(Dashboard.this, LoginActivity.class);

            startActivity(intent);
            Dashboard.this.finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Context context = Dashboard.this;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("logged_in", logged_in);

        editor.commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username = savedInstanceState.getString("username");
        password = savedInstanceState.getString("password");
        logged_in = savedInstanceState.getBoolean("logged_in");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username);
        outState.putString("password", password);
        outState.putBoolean("logged_in", logged_in);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Context context = Dashboard.this;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "none");
        password = sharedpreferences.getString("password", "none");
        logged_in = sharedpreferences.getBoolean("logged_in", logged_in);


        if (password.equals("none") && username.equals("none") && !logged_in) {
            //enviar al login
            Intent intent = new Intent(Dashboard.this, LoginActivity.class);

            startActivity(intent);
            Dashboard.this.finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webview = (WebView) getCurrentFocus().findViewById(R.id.webview2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    createWebPrintJob(webview);
                } else {
                    Toast.makeText(Dashboard.this, getText(R.string.min_version_kitkat), Toast.LENGTH_LONG).show();
                }

            }

        });
        FloatingActionButton syncronizarfab = (FloatingActionButton) findViewById(R.id.fabsync);
        syncronizarfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webview = (WebView) getCurrentFocus().findViewById(R.id.webview2);
                webview.reload();
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.cerrar_sesion) {
            logout();

            return true;
        }

        if (id == R.id.mi_cuenta) {
            return true;
        }

        if (id == R.id.salir) {
            salir();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        WebView webview2;
        ProgressBar bar;

        public PlaceholderFragment() {


        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_consultas, container, false);


            loadWeb(rootView);


            return rootView;
        }


        public void loadWeb(View rootView) {


            int position = getArguments().getInt(ARG_SECTION_NUMBER);

            webview2 = rootView.findViewById(R.id.webview2);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // chromium, enable hardware acceleration
                webview2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                // older android version, disable hardware acceleration
                webview2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            bar = rootView.findViewById(R.id.bar);
            WebSettings webSettings = webview2.getSettings();
            webSettings.setAppCacheEnabled(false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setAllowFileAccess(true);

            //webSettings.setBlockNetworkLoads(false);
            //webSettings.setLoadWithOverviewMode(true);
            String urlToLoad = "";
            String url_action = "";
            String app_url = getString(R.string.app_url);
            switch (position) {
                case 0:
                    url_action = "retenciones";
                    break;
                case 1:
                    url_action = "pagos";
                    break;
                case 2:
                    url_action = "registro";
                    break;
                default:
                    break;
            }

            if (!username.equals("none") && !password.equals("none")) {
                urlToLoad = app_url + "main/login/" + username + "/" + password + "/" + url_action;

            }

            webview2.loadUrl(urlToLoad);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);


            webview2.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView webview, String url, Bitmap favicon) {
                    super.onPageStarted(webview, url, favicon);
                    bar.setVisibility(View.VISIBLE);
                    webview2.setVisibility(View.GONE);

                    if (url.contains("PDF") || url.contains("pdf")) {
                        Toast.makeText(getContext(), "Descargando...", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.allowScanningByMediaScanner();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "acondesa_download_file.pdf");
                        //request.setVisibleInDownloadsUi(true);
                        //request.setRequiresCharging(true);


                        DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

                        dm.enqueue(request);

                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    super.onPageFinished(view, url);
                    bar.setVisibility(View.GONE);
                    webview2.setVisibility(View.VISIBLE);
                    //swipe.setRefreshing(false);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {


                    if (Uri.parse(url).getHost().contains("acondesa.com.co")) {
                        // This is my web site, so do not override; let my WebView load the page
                        // Toast.makeText(getContext(), "Sigo dentro de acondesa", Toast.LENGTH_LONG).show();

                        return false;//aqui debe ser false,
                    }
                    // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

                    Toast.makeText(getContext(), "saliendo de acondesaApp url:" + url, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    view.loadUrl("file:///android_asset/conection_error.html");
                }

            });
        }
    }

    @Override
    public void onBackPressed() {
        WebView myWebView = (WebView) findViewById(R.id.webview2);
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        }

    }


    public static class WebAppInterface {
        Context mContext;
        View view;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c, View v) {
            mContext = c;
            view = v;
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "RETENCIONES";
                case 1:
                    return "PAGOS";
                case 2:
                    return "REGISTRO";
            }
            return null;
        }
    }

    private void logout() {
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        String app_url = getString(R.string.app_url);
        WebView webview = (WebView) findViewById(R.id.webview2);
        webview.loadUrl(app_url + "sessions/logout");
        logged_in = false;
        Intent intent = new Intent(Dashboard.this, LoginActivity.class);

        startActivity(intent);
        Dashboard.this.finish();

    }


    private void createWebPrintJob(WebView webView) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Screen Capture";
        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

    }

    private void salir() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Importante");
        dialog.setMessage(getString(R.string.exit_message_prompt));
        dialog.setCancelable(false);
        dialog.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }//do some
        });
        dialog.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }//do some
        });
        dialog.show();
    }

}
