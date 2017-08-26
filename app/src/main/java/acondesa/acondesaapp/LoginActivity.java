package acondesa.acondesaapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private boolean logged_in = false;
    private ProgressBar login_progress;
    private Button login_button;

    private static String username_st;
    private static String password_st;
    private static TextView register_link;
    private static TextView ubication_link;
    private static TextView contact_link;

    private static final String MyPREFERENCES = "MyPrefs";
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username_st);
        outState.putString("password", password_st);
        outState.putBoolean("logged_in", logged_in);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username_st = savedInstanceState.getString("username");
        password_st = savedInstanceState.getString("password");
        logged_in = savedInstanceState.getBoolean("logged_in");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!isConnected(LoginActivity.this)) {
            buildDialog(LoginActivity.this, getText(R.string.title_error_noconection).toString(), getText(R.string.error_noconection_message).toString())
                    .show().setCanceledOnTouchOutside(false);

        } else {

            setContentView(R.layout.activity_login);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            username = (EditText) findViewById(R.id.email);
            password = (EditText) findViewById(R.id.password);
            login_progress = (ProgressBar) findViewById(R.id.login_progress);
            login_button = (Button) findViewById(R.id.email_sign_in_button);

            register_link = (TextView) findViewById(R.id.textview_register);
            ubication_link = (TextView) findViewById(R.id.textview_ubication);
            contact_link = (TextView) findViewById(R.id.textview_contact);
            textViewLinks();
            LoginButton();
        }
    }

    private void textViewLinks(){
        register_link.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerActivity();
                    }
                }
        );

        ubication_link.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ubicationActivity();
                    }
                }
        );

        contact_link.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactActivity();
                    }
                }
        );
    }

    private void LoginButton() {

        login_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkLogin();
                    }
                }
        );
    }


    private  void registerActivity(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

        startActivity(intent);
    }

    private  void ubicationActivity(){
        Intent intent = new Intent(LoginActivity.this, UbicationActivity.class);

        startActivity(intent);

    }

    private  void contactActivity(){
        Intent intent = new Intent(LoginActivity.this, ContactActivity.class);

        startActivity(intent);

    }

    private void checkLogin() {


        final String user = username.getText().toString();
        final String password2 = password.getText().toString();

        // Inicializar AsyncLogin() class con user y pass
        new AsyncLogin().execute(user, password2);

    }

    private class AsyncLogin extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            login_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Url del php
                url = new URL("http://www.acondesa.com.co/proyectoandroid/application/controllers/login.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();


                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    //para debug
                    // return "unsuccesful: "+Integer.toString(response_code)+" Message: "+response_message+" url: "+query2+" os: "+writer2;
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            login_progress.setVisibility(View.GONE);

            if (result.equalsIgnoreCase("true")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                //para debug
                Context context = LoginActivity.this;
                SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("username", username.getText().toString());
                editor.putString("password", password.getText().toString());

                editor.apply();
                logged_in = true;

                Intent intent = new Intent(LoginActivity.this, Dashboard.class);

                startActivity(intent);
                LoginActivity.this.finish();


            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                Toast.makeText(LoginActivity.this, getText(R.string.error_invalid_data).toString(), Toast.LENGTH_LONG).show();
                //para debug
                //buildDialog(LoginActivity.this,"Response",result.toString()).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(LoginActivity.this, getText(R.string.unknown_error).toString(), Toast.LENGTH_LONG).show();
                //para debug
                //buildDialog(LoginActivity.this,"Response",result.toString()).show();

            } else {
                //mostrar respuesta del server (solo para debug)
                buildDialog(LoginActivity.this, "Response", result).show();

            }
        }

    }

    private boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        } else {
            // not connected to the internet
            return false;
        }
        return true;
    }

    private AlertDialog.Builder buildDialog(Context c, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(getText(R.string.button_error_noconection).toString(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cerrar todo lo que haya que cerrar
                finish();
            }


        });


        return builder;
    }
}