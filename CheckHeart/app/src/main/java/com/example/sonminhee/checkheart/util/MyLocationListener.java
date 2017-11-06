package com.example.sonminhee.checkheart.util;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.example.sonminhee.checkheart.activity.HeartCheckActivity;

/**
 * Created by areum on 2017-10-12.
 */

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        ((HeartCheckActivity)HeartCheckActivity.mContext).setGPS(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(HeartCheckActivity.Activity, provider + " Disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(HeartCheckActivity.this, provider + " Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case GpsStatus.GPS_EVENT_STARTED:
                //Toast.makeText(HeartCheckActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                //Toast.makeText(HeartCheckActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                //Toast.makeText(HeartCheckActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //Toast.makeText(HeartCheckActivity.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}