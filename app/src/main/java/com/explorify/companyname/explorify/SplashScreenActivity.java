package com.explorify.companyname.explorify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreenActivity extends Activity {

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5 * 1000);

                    if (!session.isLoggedIn()) {
                        // Redirect to another intent
                        Intent i = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);

                    } else {



                        // After 5 seconds redirect to another intent
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                    }



                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
