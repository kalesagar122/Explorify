package com.explorify.companyname.explorify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.explorify.companyname.explorify.Constants.Base_Url;
import static com.explorify.companyname.explorify.Constants.Base_Url1;
import static com.explorify.companyname.explorify.Constants.GOOGLE_PROJ_ID;


@SuppressWarnings({"ALL", "deprecation"})
public class CategoryActivity extends ActionBarActivity {

    // Session Manager Class
    SessionManager session;
    LinearLayout linearMain;
    CheckBox checkBox;
    ArrayList<String> AllCategory;
    private View mProgressView;
    private View mCategoryFormView;

    Context applicationContext;

    // GCM
    GoogleCloudMessaging gcmObj;
    //Context applicationContext;
    String regId = "";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        applicationContext = getApplicationContext();

        // Session class instance
        session = new SessionManager(getApplicationContext());

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("Main")){
            // get action bar
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();


            // Enabling Up / Back navigation
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressView = findViewById(R.id.category_progress);
        mCategoryFormView = findViewById(R.id.category_form);

        linearMain = (LinearLayout) findViewById(R.id.linearMain);

        Button mSaveButton = (Button) findViewById(R.id.category_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCategorySave();
            }
        });

        showProgress(true);


        AllCategory = new ArrayList<String>();

        JsonArrayRequest movieReq = new JsonArrayRequest(Base_Url + "Category",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showProgress(false);

                        if (response != null)
                        {

                            try {

                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);

                                    AllCategory.add(jsonObject.getString("Id"));

                                    String name = jsonObject.getString("CategoryName");
                                    if (name.equals("Offers")) {
                                        checkBox = new CheckBox(applicationContext);
                                        checkBox.setId(i);
                                        checkBox.setChecked(true);
                                        checkBox.setEnabled(false);
                                        checkBox.setTextColor(R.color.black);
                                        checkBox.setTextSize(15);
                                        checkBox.setText(jsonObject.getString("CategoryName"));
                                        linearMain.addView(checkBox);
                                    } else {
                                        checkBox = new CheckBox(applicationContext);
                                        checkBox.setId(i);
                                        checkBox.setChecked(true);
                                        checkBox.setTextColor(R.color.black);
                                        checkBox.setTextSize(15);
                                        checkBox.setText(jsonObject.getString("CategoryName"));
                                        linearMain.addView(checkBox);
                                    }
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    //Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                    // get user data from session
                    HashMap<String, String> user = session.getUserDetails();

                    HashMap<String, String> headers = new HashMap<String, String>();
                    attemptLogin(user.get(SessionManager.KEY_EMAIL),user.get(SessionManager.KEY_PASSWORD));

                } else if (error instanceof ServerError) {
                    Toast.makeText(applicationContext, "Server Error. Please try again!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                showProgress(false);

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + user.get(SessionManager.KEY_TOKEN));
//                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);


        showProgress(false);



    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void attemptLogin(final String email,final String password)
    {
        StringRequest movieReq = new StringRequest(Request.Method.POST, Base_Url1 + "Token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //showProgress(false);

                        if (response != null)
                        {
                            try {
                                JSONObject json = new JSONObject(response);

                                // Creating user login session
                                session.createLoginSession(email, password, json.getString("access_token"));

                                // Check if Google Play Service is installed in Device
                                // Play services is needed to handle GCM stuffs
                                if (checkPlayServices()) {

                                    // Register Device in GCM Server
                                    registerInBackground(email);
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    showProgress(false);
                    Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    showProgress(false);
                    Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {

                    showProgress(false);
                    Toast.makeText(applicationContext, "Server error. Please contact to administrator.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    showProgress(false);
                    Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    showProgress(false);
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                else {
                    showProgress(false);
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();
                }

            }
        }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Username", email);
                params.put("Password", password);
                params.put("grant_type", "password");
                return params;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);
    }

    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } /*else {
            Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }*/
        return true;
    }

    // AsyncTask to register Device in GCM Server
    private void registerInBackground(final String emailID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(applicationContext, regId, emailID);
                    /*Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();*/
                } else {
                    Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    // Store  RegId and Email entered by User in SharedPref
    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID) {
        storeRegIdinServer();

    }

    // Share RegID with GCM Server Application (Php)
    private void storeRegIdinServer() {
        //showProgress(true);
        /*mUserGcmTask = new UserGCMRegTask(regId);
        mUserGcmTask.execute((Void) null);*/

        StringRequest movieReq = new StringRequest(Request.Method.POST, Base_Url + "gcmregistration",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);

                        if (response != null)
                        {
                            // Registered the GCM Reg Id Successfully

                            // Redirect to another intent
                            Intent i = new Intent(getBaseContext(), CategoryActivity.class);
                            startActivity(i);

                            //Remove activity
                            finish();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(applicationContext, "Server Error. Please try again!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                showProgress(false);

            }
        }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("RegId", regId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + user.get(SessionManager.KEY_TOKEN));
//                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);


    }

    /**
     * Attempts to save category
     */
    public void attemptCategorySave() {
        showProgress(true);

        final ArrayList<String> AllCheckbox = new ArrayList<String>();
        for (int i = 0; i < linearMain.getChildCount(); i++) {
            View nextChild = linearMain.getChildAt(i);

            if (nextChild instanceof CheckBox) {
                CheckBox check = (CheckBox) nextChild;
                if (check.isChecked()) {
                    AllCheckbox.add(AllCategory.get(i));
                }
            }
        }




        StringRequest movieReq = new StringRequest(Request.Method.POST, Base_Url + "Category",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);

                        if (response != null)
                        {
                            // Redirect to another intent
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(i);

                            //Remove activity
                            finish();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(applicationContext, "Server Error. Please try again!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                showProgress(false);

            }
        }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                for (int index = 0; index < AllCheckbox.size(); index++) {
                    params.put("category[" + index + "]", AllCheckbox.get(index));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + user.get(SessionManager.KEY_TOKEN));
//                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();


        showProgress(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == 16908332) {

            //this.onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCategoryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCategoryFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCategoryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCategoryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
