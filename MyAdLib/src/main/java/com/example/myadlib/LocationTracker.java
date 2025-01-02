package com.example.myadlib;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Description: this class tracks the location of the device
 */
public class LocationTracker extends Service implements LocationListener {
    protected static final String TAG = "Coursework: LocationTracker";
    private final Context mContext;
    private static final long MIN_DISTANCE_VARIANCE = 10;
    private static final long MIN_TIME_VARIANCE = 1000 * 60;
    protected LocationManager locationManager;
    private boolean isGPSEnabled = false;
    Location location;

    public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
            Log.d(TAG, "No GPS location provider available!");
            return null;
        }
        Log.d(TAG, "GPS Location available!");
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this.mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permission granted from the user to access location!");
        }
        else if (location == null) {
            // missing access fine location permission warning suppressed
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_VARIANCE, MIN_DISTANCE_VARIANCE, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        else{
            Log.w(TAG,"the location is already fetched");
        }
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
