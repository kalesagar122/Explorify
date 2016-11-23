package com.explorify.companyname.explorify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.explorify.companyname.explorify.Constants.Base_Url;
import static com.explorify.companyname.explorify.Constants.Base_Url1;
import static com.explorify.companyname.explorify.Constants.GOOGLE_PROJ_ID;


/**
 * A login screen that offers login via email/password.
 */
@SuppressWarnings("ALL")
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
   /* private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };*/
    public static final String REG_ID = "regId";
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // Session Manager Class
    SessionManager session;
    // GCM
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    /*private UserLoginTask mAuthTask = null;
    private UserGCMRegTask mUserGcmTask = null;*/
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Matcher matcher;
    private Pattern pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        applicationContext = getApplicationContext();

        pattern = Pattern.compile(PASSWORD_PATTERN);

        // User Session Manager
        session = new SessionManager(getApplicationContext());

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mForgotPasswordButton = (Button) findViewById(R.id.email_forgot_password_button);
        mForgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to another intent
                Intent i = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            /*mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/

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
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String err = jsonObject.getString("error");
                            if (err.contains("invalid_email")) {

                                RegisterUser(email,password);
                            }
                            if (err.contains("invalid_grant")) {
                                showProgress(false);
                                // Incorrect Password

                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();

                            }
                            if (err.contains("invalid_email_id_confirmation")) {
                                showProgress(false);

                                // Already registered with the email address but yet not confirm the email address

                                // Redirect to another intent
                                Intent i = new Intent(getBaseContext(), EmailVerificationActivity.class);
                                i.putExtra("Email",email);
                                i.putExtra("Password", password);
                                startActivity(i);
                            }

                            //Toast.makeText(applicationContext, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            //Handle a malformed json response
                        } catch (UnsupportedEncodingException errorw) {

                        }

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
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    private boolean isPasswordValidText(String password) {
        //TODO: Replace this with your own logic
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void RegisterUser(final String rEmail,final String rPassword)
    {
        StringRequest movieReq = new StringRequest(Request.Method.POST, Base_Url + "Account/Register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);

                        if (response != null)
                        {

                            attemptLogin();
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
                params.put("Email", rEmail);
                params.put("Password", rPassword);
                params.put("ConfirmPassword", rPassword);
                return params;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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
        /*SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.commit();*/
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
                            Intent i = new Intent(getBaseContext(), UserProfileActivity.class);
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, ResponseModel> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected ResponseModel doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            ResponseModel rm = new ResponseModel();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Base_Url1 + "Token");

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("Username", mEmail));
                nameValuePairs.add(new BasicNameValuePair("Password", mPassword));
                nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);


                StatusLine status = response.getStatusLine();
                int statusCode = status.getStatusCode();

                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                if (statusCode == 400) {
                    JSONObject json = new JSONObject(builder.toString());
                    String err = json.getString("error");
                    if (err.contains("invalid_email")) {
                        // TODO: register the new account here.

                        //String content = json.getString("invalid_email");
                        //do something with content string

                        HttpPost httppost1 = new HttpPost(Base_Url + "Account/Register");

                        // Add your data
                        List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(3);
                        nameValuePairs1.add(new BasicNameValuePair("Email", mEmail));
                        nameValuePairs1.add(new BasicNameValuePair("Password", mPassword));
                        nameValuePairs1.add(new BasicNameValuePair("ConfirmPassword", mPassword));
                        httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1));

                        // Execute HTTP Post Request
                        HttpResponse response1 = httpclient.execute(httppost1);

                        StatusLine status1 = response1.getStatusLine();
                        int statusCode1 = status1.getStatusCode();

                        InputStream jsonStream1 = response1.getEntity().getContent();
                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(jsonStream1));

                        StringBuilder builder1 = new StringBuilder();
                        String line1;

                        while ((line1 = reader1.readLine()) != null) {
                            builder1.append(line1);
                        }

                        rm.setStatus(100);
                        //rm.setResponce(builder1.toString());
                    }
                    if (err.contains("invalid_grant")) {
                        // Incorrect Password

                        *//*String content = json.getString("content");
                        //do something with content string*//*

                        rm.setStatus(101);
                        rm.setResponce(builder.toString());
                    }
                    if (err.contains("invalid_email_id_confirmation")) {
                        // Already registered with the email address but yet not confirm the email address

                        *//*String content = json.getString("content");
                        //do something with content string*//*

                        rm.setStatus(102);
                        //rm.setResponce(builder.toString());
                    }
                } else {
                    // TODO: Send a Login Access token.

                    rm.setStatus(statusCode);
                    JSONObject json = new JSONObject(builder.toString());
                    rm.setResponce(json.getString("access_token"));
                }
            } catch (Exception Ex) {

            }

            return rm;
        }

        @Override
        protected void onPostExecute(final ResponseModel success) {
            mAuthTask = null;
            showProgress(false);

            if (success.getStatus() == 200) {
                // Login Successful with return Access token

                // Creating user login session
                session.createLoginSession(mEmail, mPassword, success.getResponce());

                // Check if Google Play Service is installed in Device
                // Play services is needed to handle GCM stuffs
                if (checkPlayServices()) {

                    // Register Device in GCM Server
                    registerInBackground(mEmail);
                }

                Intent i=new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);

                //Remove activity
                finish();*//*

            } else if (success.getStatus() == 100) {
                // Register the new account and show confirm email address

                // Redirect to another intent
                Intent i = new Intent(getBaseContext(), EmailVerificationActivity.class);
                startActivity(i);
            } else if (success.getStatus() == 101) {
                // Incorrect Password

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else {
                // Already registered with the email address but yet not confirm the email address

                // Redirect to another intent
                Intent i = new Intent(getBaseContext(), EmailVerificationActivity.class);
                i.putExtra("Email",mEmail);
                i.putExtra("Password",mPassword);
                startActivity(i);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserGCMRegTask extends AsyncTask<Void, Void, ResponseModel> {

        private final String uRegId;

        UserGCMRegTask(String regid) {
            uRegId = regid;
        }

        @Override
        protected ResponseModel doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            ResponseModel rm = new ResponseModel();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Base_Url + "gcmregistration");

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("RegId", uRegId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                httppost.addHeader("Authorization", "Bearer " + user.get(SessionManager.KEY_TOKEN));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);


                StatusLine status = response.getStatusLine();
                int statusCode = status.getStatusCode();

                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                rm.setStatus(statusCode);
                rm.setResponce(builder.toString());
            } catch (Exception Ex) {

            }

            return rm;
        }

        @Override
        protected void onPostExecute(final ResponseModel success) {
            mAuthTask = null;
            showProgress(false);

            if (success.getStatus() == 200) {
                // Registered the GCM Reg Id Successfully

                // Redirect to another intent
                Intent i = new Intent(getBaseContext(), CategoryActivity.class);
                startActivity(i);

                //Remove activity
                finish();

            }
            // When Http response code is '404'
            else if (success.getStatus() == 404) {
                Toast.makeText(applicationContext,
                        "Requested resource not found",
                        Toast.LENGTH_LONG).show();
            }
            // When Http response code is '500'
            else if (success.getStatus() == 500) {
                Toast.makeText(applicationContext,
                        "Something went wrong at server end",
                        Toast.LENGTH_LONG).show();
            }
            // When Http response code other than 404, 500
            else {
                Toast.makeText(
                        applicationContext,
                        "Unexpected Error occcured! [Most common Error: Device might "
                                + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }*/
}

