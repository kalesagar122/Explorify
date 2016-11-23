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
import android.widget.ShareActionProvider;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.explorify.companyname.explorify.Constants.Base_Url;
import static com.explorify.companyname.explorify.Constants.Actual_Url;
import static com.explorify.companyname.explorify.Constants.Base_Url1;
import static com.explorify.companyname.explorify.Constants.GOOGLE_PROJ_ID;

@SuppressWarnings({"ALL", "deprecation"})
public class NewsDetailsActivity extends ActionBarActivity {

    // Session Manager Class
    SessionManager session;
    String Id;

    private View mProgressView;

    Context applicationContext;

    ShareActionProvider mShareActionProvider;
    String mTitle;
    String mDescription;
    String mImg;

    // GCM
    GoogleCloudMessaging gcmObj;
    //Context applicationContext;
    String regId = "";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        applicationContext = getApplicationContext();

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("Id")){

            // Session class instance
            session = new SessionManager(getApplicationContext());

            Id = myIntent.getStringExtra("Id");


            /*TextView mText = (TextView)findViewById(R.id.textView1);
            mText.setText("Welcome "+myIntent.getStringExtra("myExtra")+"!");*/
        }

        mProgressView = findViewById(R.id.news_details_progress);

        showProgress(true);

        // get action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Toast.makeText(applicationContext, Id.toString(), Toast.LENGTH_SHORT).show();
        JsonObjectRequest movieReq = new JsonObjectRequest(Base_Url + "Jobs/GetJobsById?id=" + Id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);

                        if (response != null)
                        {

                            try {
                                TextView mTextTitle = (TextView) findViewById(R.id.title);
                                mTextTitle.setText(response.getString("JobTitle"));
                                mTitle = response.getString("JobTitle");

                                TextView mTextDescription = (TextView) findViewById(R.id.description);
                                mTextDescription.setText(response.getString("JobDetails"));
                                mDescription = response.getString("JobDetails");

                                TextView mTextPostDate = (TextView) findViewById(R.id.newsposteddate);

                                String source = response.getString("PostedDate");
                                //Toast.makeText(applicationContext, source, Toast.LENGTH_SHORT).show();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat df2 = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                                Date formatted = null;
                                try {
                                    formatted = formatter.parse(source);
                                    //String formattedString = formatted.toString();
                                    mTextPostDate.setText("Posted on - " + df2.format(formatted));
                                    //mTextPostDate.setText(df2.format(formatted));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                TextView mTextExpireDate = (TextView) findViewById(R.id.expireDate);

                                String source1 = response.getString("ExpireDate");
                                //Toast.makeText(applicationContext, source, Toast.LENGTH_SHORT).show();
                                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat df3 = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                                Date formatted1 = null;
                                try {
                                    formatted1 = formatter1.parse(source1);
                                    //String formattedString = formatted.toString();
                                    //mTextExpireDate.setText("Posted on - " + df3.format(formatted1));
                                    mTextExpireDate.setText("Expire on - " + df3.format(formatted1));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                TextView mTextCompanyJobId = (TextView) findViewById(R.id.companyJobId);
                                mTextCompanyJobId.setText("Company Job Id - " + response.getString("CompanyJobId"));
                                //mDescription = response.getString("JobDetails");

                                TextView mTextExperience = (TextView) findViewById(R.id.experience);
                                mTextExperience.setText("Year & Month of Experience - " + response.getString("YearExpereince") + " Years & "+ response.getString("MonthExperience") + " Months" );

                                TextView mTextSkills = (TextView) findViewById(R.id.skills);
                                mTextSkills.setText("Skills - " + response.getString("Skills"));

                                TextView mTextSMatch = (TextView) findViewById(R.id.matchRatingTest);
                                mTextSMatch.setText("Match - " + response.getString("Rating") + " %");

                                TextView mTextCompanyName = (TextView) findViewById(R.id.companyName);
                                mTextCompanyName.setText("Company Name - " + response.getString("CompanyName"));

                                TextView mTextWebsite = (TextView) findViewById(R.id.website);
                                mTextWebsite.setText("Website - " + response.getString("Website"));

                                TextView mTextAddress = (TextView) findViewById(R.id.address);
                                mTextAddress.setText(response.getString("CompanyAddress"));

                                /*TextView mTextExpireDate = (TextView) findViewById(R.id.expiredate);
                                mTextExpireDate.setText(response.getString("ExpireDate"));*/

                                if (imageLoader == null)
                                    imageLoader = MyApplication.getInstance().getImageLoader();


                                FeedImageView feedImageView = (FeedImageView) findViewById(R.id.feedimg);
                                JSONArray jsonarray = (JSONArray) response.get("PostNewsImages");

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    final JSONObject jsonObject1 = jsonarray.getJSONObject(i);

                                    feedImageView.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v) {
                                            try {
                                            Intent intent = new Intent(applicationContext, OpenImageActivity.class);
                                            intent.putExtra("path", jsonObject1.getString("ImagePath"));
                                            startActivity(intent);
                                            }catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    feedImageView.setVisibility(View.VISIBLE);
                                    mImg = jsonObject1.getString("ImagePath");
                                    feedImageView.setImageUrl(Actual_Url + jsonObject1.getString("ImagePath"), imageLoader);
                                    feedImageView
                                            .setResponseObserver(new FeedImageView.ResponseObserver() {
                                                @Override
                                                public void onError() {
                                                }

                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
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
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                HashMap<String, String> headers = new HashMap<>();
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


    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_details, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            Intent intent=new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide what to do with it.
            //intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_TEXT, mTitle + System.getProperty("line.separator") +"Source : http://localification.com");
            /*if(mImg != null) {
                intent.putExtra(Intent.EXTRA_STREAM, mImg);
            }*/
            startActivity(Intent.createChooser(intent, "Share Via"));

            return true;
        }
        if (id == 16908332) {

            //this.onBackPressed();
            finish();
            return true;
        }

        if (id == R.id.refresh_news_activity) {

            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
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
        }
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
}
