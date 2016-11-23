package com.explorify.companyname.explorify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceReceiver extends BroadcastReceiver {

    protected static final String TAG = "geofence-transitions";

    final static String GROUP_KEY_GEOFENCE = "group_key_localification";

    Context context;

    /*Intent broadcastIntent = new Intent();*/

    public GeofenceReceiver() {
    }

    /*public static GeofenceModel getGeofenceModel(String id) {
        return new Select()
                .from(GeofenceModel.class)
                .where("GId = ?", id)
                .executeSingle();
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        this.context = context;

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            //String[] geofenceIds = new String[triggeringGeofences.size()];

            // We can remove this loop since we are using just one id
            // (with more this is necessary)
            for (int i = 0; i < triggeringGeofences.size(); i++) {
                //String placeId = triggeringGeofences.get(i).getRequestId();
                List<GeofenceModel> fg = GeofenceModel.getAllGeofences();
                GeofenceModel geofence = GeofenceModel.getGeofenceModel(triggeringGeofences.get(i).getRequestId());
                if (geofence != null) {
                    sendNotification(geofence.Title, geofence.Description, geofence.GId);
                }
            }


        } else {
            // Log the error.
            Log.e(TAG, "Geofence transition error: invalid transition type : " + geofenceTransition);
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }


    private void sendNotification(String title, String description, String id) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, NewsDetailsActivity.class);
        notificationIntent.putExtra("Id", id);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Default Sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(notificationPendingIntent)
                .setGroup(GROUP_KEY_GEOFENCE);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                notifyID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (notifyID < 9) {
            notifyID = notifyID + 1;
        } else {
            notifyID = 0;
        }
        builder.setContentIntent(resultPendingIntent);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(notifyID, builder.build());
    }


}
