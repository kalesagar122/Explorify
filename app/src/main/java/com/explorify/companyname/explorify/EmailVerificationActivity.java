package com.explorify.companyname.explorify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import static com.explorify.companyname.explorify.Constants.Base_Url;


public class EmailVerificationActivity extends Activity {

    String mEmail;
    String mPassword;
    //private ProgressDialog pDialog;
    Context applicationContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        applicationContext = getApplicationContext();

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("Email") && myIntent.hasExtra("Password")) {

            mEmail = myIntent.getStringExtra("Email");
            mPassword = myIntent.getStringExtra("Password");

            TextView mEmailResendText = (TextView) findViewById(R.id.email_resend_text);
            String text = "We have send you verification link at "+ mEmail +" to confirm your email address. Please go to your email account and check your inbox or span folder and click on the verify link.";

            mEmailResendText.setText(text);
        }

        Button mEmailResendButton = (Button) findViewById(R.id.resend_email_button);
        mEmailResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendemail();
            }
        });

        Button mBacktoLoginButton = (Button) findViewById(R.id.backto_login_button);
        mBacktoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }



    public void sendemail()
    {

        /*pDialog = new ProgressDialog(getBaseContext());
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);*/


        // Creating volley request obj
        StringRequest movieReq = new StringRequest(Request.Method.POST,Base_Url+ "Account/ResendEmailConfirmation" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hidePDialog();

                        if (response != null)
                        {
                            // Redirect to another intent
                            Intent i = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(i);
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(EmailVerificationActivity.this.applicationContext, "Either requested url is timeout or notwork is disconnected.", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(EmailVerificationActivity.this.applicationContext, "Authorization Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(EmailVerificationActivity.this.applicationContext, "Server Error. Please try again!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(EmailVerificationActivity.this.applicationContext, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(EmailVerificationActivity.this.applicationContext, "Error in response!", Toast.LENGTH_SHORT).show();

                }
                //hidePDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", mEmail);
                params.put("Password", mPassword);
                params.put("ConfirmPassword", mPassword);
                return params;
            }
        };

        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);

    }

    /*private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }*/
}
