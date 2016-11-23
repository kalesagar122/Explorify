package com.explorify.companyname.explorify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.explorify.companyname.explorify.Constants.Base_Url;
import static com.explorify.companyname.explorify.Constants.Base_Url1;
import static com.explorify.companyname.explorify.Constants.GOOGLE_PROJ_ID;
/**
 * Created by sagar and sayali on 10-2-2016.
 */

@SuppressWarnings("ALL")
public class MainActivity extends ActionBarActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // Session Manager Class
    SessionManager session;
    // GCM
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    private View mProgressView;
    private View mMainFormView;

    private ProgressDialog pDialog;

    FragmentPagerItemAdapter adapter;

    String regId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationContext = getApplicationContext();

        mMainFormView = findViewById(R.id.main_form);
        mProgressView = findViewById(R.id.main_progress);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // get user data from session
        HashMap<String, String> user1 = session.getUserDetails();

        showProgress(true);



        final FragmentPagerItems pages = new FragmentPagerItems(this);

        // get user data from session
        final HashMap<String, String> user = session.getUserDetails();

        JsonArrayRequest movieReq = new JsonArrayRequest(Base_Url + "Category",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showProgress(false);

                        if (response != null) {

                            try {

                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject jsonObject = response.getJSONObject(i);


                                    Bundle args = new Bundle();
                                    args.putString("param1", jsonObject.getString("Id"));
                                    args.putString("param4", String.valueOf(user.get(SessionManager.KEY_TOKEN)));
                                    CharSequence name = jsonObject.getString("CategoryName");
                                    pages.add(FragmentPagerItem.of(name, NewsFragment.class, args));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            adapter = new FragmentPagerItemAdapter(
                                    getSupportFragmentManager(), pages);

                            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                            viewPager.setAdapter(adapter);

                            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
                            viewPagerTab.setViewPager(viewPager);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    //Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                    // get user data from session
                    HashMap<String, String> user = session.getUserDetails();

                    HashMap<String, String> headers = new HashMap<String, String>();
                    attemptLogin(user.get(SessionManager.KEY_EMAIL), user.get(SessionManager.KEY_PASSWORD));

                } else if (error instanceof ServerError) {
                    Toast.makeText(applicationContext, "Server Error. Please try again!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                showProgress(false);

            }
        }) {

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

    @Override
    public void onStart() {
        super.onStart();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            //pDialog = null;
        }

        //setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            showProgress(true);
            //setProgressBarIndeterminateVisibility(true);

            StringRequest movieReq = new StringRequest(Request.Method.POST, Base_Url + "Account/Logout",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showProgress(false);

                            if (response != null) {
                                // Clear the session data
                                // This will clear all session data and
                                // redirect user to LoginActivity
                                session.logoutUser();

                                // Check if Google Play Service is installed in Device
                                // Play services is needed to handle GCM stuffs
                                if (checkPlayServices()) {

                                    // Register Device in GCM Server
                                    unregisterInBackground();
                                }

                                // Redirect to another intent
                                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(i);

                                //Remove activity
                                finish();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof AuthFailureError) {
                        //Toast.makeText(applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                        // get user data from session
                        HashMap<String, String> user = session.getUserDetails();

                        HashMap<String, String> headers = new HashMap<String, String>();
                        attemptLogin(user.get(SessionManager.KEY_EMAIL), user.get(SessionManager.KEY_PASSWORD));

                    } else if (error instanceof ServerError) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonObject = new JSONObject(responseBody);
                            Toast.makeText(applicationContext, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            //Handle a malformed json response
                        } catch (UnsupportedEncodingException errorw) {

                        }

                    } else if (error instanceof NetworkError) {
                        Toast.makeText(applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ParseError) {
                        Toast.makeText(applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                    }
                    showProgress(false);

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
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



            return true;
        }

        if (id == R.id.action_category) {

            // Redirect to another intent
            Intent i = new Intent(getBaseContext(), CategoryActivity.class);
            i.putExtra("Main", "Main");
            startActivity(i);
        }

        if (id == R.id.action_userProfile) {

            // Redirect to another intent
            Intent i = new Intent(getBaseContext(), UserProfileActivity.class);
            i.putExtra("Main", "Main");
            startActivity(i);
        }

        if (id == R.id.refresh_activity) {
            finish();
            startActivity(getIntent());
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
        }
    }

    // AsyncTask to register Device in GCM Server
    private void unregisterInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    gcmObj.unregister();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
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
}
