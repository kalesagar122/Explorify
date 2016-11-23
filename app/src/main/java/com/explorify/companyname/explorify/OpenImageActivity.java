package com.explorify.companyname.explorify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;

import static com.explorify.companyname.explorify.Constants.Actual_Url;


public class OpenImageActivity extends ActionBarActivity {

    private View mProgressView;

    Context applicationContext;

    String mPath;

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    TouchImageView feedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("path")) {

            mPath = Actual_Url + myIntent.getStringExtra("path");




            if (imageLoader == null)
                imageLoader = MyApplication.getInstance().getImageLoader();

            feedImageView = (TouchImageView) findViewById(R.id.open_img);

            feedImageView.setVisibility(View.VISIBLE);
            feedImageView.setImageUrl(mPath, imageLoader);
            feedImageView
                    .setResponseObserver(new TouchImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });


        }
        //showProgress(true);

        // get action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);

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
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            Drawable mDrawable = feedImageView.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    mBitmap, "Image", null);

            Uri uri = Uri.parse(path);

            //Uri imageUri = Uri.parse(mPath);

            // Add data to the intent, the receiving app will decide what to do with it.
            //intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
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
        return super.onOptionsItemSelected(item);
    }
}
