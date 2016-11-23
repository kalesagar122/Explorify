package com.explorify.companyname.explorify;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GcmBroadcastReceiver extends BroadcastReceiver implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "monitor-geofences";
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private Context mContext;
    private SimpleGeofence mSimplegeofence;


    private String mId;

    /**
     * Used to keep track of whether geofences were added.
     *//*
    private boolean mGeofencesAdded;*/

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle extras = intent.getExtras();

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        //Toast.makeText(mContext, "Receive Push Notification", Toast.LENGTH_SHORT).show();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }

        // Remove Expire Geofences.
        GeofenceModel.getExpireGeofences();


        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                //.setRequestId(intent.getStringExtra("id"))
                .setRequestId(extras.getString("id"))
                        // Set the circular region of this geofence.
                .setCircularRegion(
                        Double.parseDouble(extras.getString("lat")),
                        Double.parseDouble(extras.getString("lng")),
                        Float.parseFloat(extras.getString("radius"))
                )
                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                .setExpirationDuration(Long.parseLong(extras.getString("millisecond")))
                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                        // Create the geofence.
                .build());


        mId = extras.getString("id");

        mSimplegeofence = new SimpleGeofence(extras.getString("id"), Double.parseDouble(extras.getString("lat")), Double.parseDouble(extras.getString("lng")),
                Float.parseFloat(extras.getString("radius")), extras.getString("expirationdate"), Geofence.GEOFENCE_TRANSITION_ENTER,
                extras.getString("title"), extras.getString("description"));

        //addGeofences();


        //throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        addGeofences();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(mContext, "GoogleApiClient no yet connected. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            );


            // Then save the new one
            GeofenceModel gm = new GeofenceModel();
            gm.GId = mSimplegeofence.getId();
            gm.Description = mSimplegeofence.getDescription();
            gm.Title = mSimplegeofence.getTitle();
            /*DateFormat format = new SimpleDateFormat("MM/d/yyyy H:m:s:S", Locale.ENGLISH);
            String startDateString = mSimplegeofence.getExpirationDuration();
            Date date = format.parse(startDateString);*/
            DateFormat df = new SimpleDateFormat("MM/dd/MM/yyyy HH:mm:ss");
            Date startDate;
            try {
                gm.Expiration = df.parse(mSimplegeofence.getExpirationDuration());
            } catch (ParseException e) {
                e.printStackTrace();
                //Toast.makeText(mContext, "Error in date format "+ mSimplegeofence.getExpirationDuration() + " dd/MM/yyyy HH:mm:ss", Toast.LENGTH_SHORT).show();
            }
            //gm.Expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mSimplegeofence.getExpirationDuration());
            gm.Lan = mSimplegeofence.getLongitude();
            gm.Lat = mSimplegeofence.getLatitude();
            gm.Radius = mSimplegeofence.getRadius();
            gm.save();

            //Toast.makeText(mContext, "Add Successfully", Toast.LENGTH_SHORT).show();


        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }


    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }


    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        //Toast.makeText(mContext, "Pending Intent", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent("com.explorify.companyname.explorify.ACTION_RECEIVE_GEOFENCE");
        //Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
