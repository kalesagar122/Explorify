package com.explorify.companyname.explorify;

/**
 * Created by sagar and sayali on 11-01-2016.
 */
public class SimpleGeofence {

    private String mId;
    private double mLatitude;
    private double mLongitude;
    private float mRadius;
    private String mExpirationDuration;
    private int mTransitionType;
    private String mTitle;
    private String mDescription;

    public SimpleGeofence(final String id, final double latitude, final double longitude, final float radius,
                          final String expiration, final int transition, final String title, final String description) {

        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mExpirationDuration = expiration;
        mTransitionType = transition;
        mTitle = title;
        mDescription = description;
    }

    public String getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public String getExpirationDuration() {
        return mExpirationDuration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
