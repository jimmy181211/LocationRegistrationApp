package com.example.myadlib;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;


public class InfoGetter{
    private static final String TAG = "Coursework:InfoGetter";
    private Context ctx;

    public InfoGetter(Context ctx){
        this.ctx=ctx;
    }

    /**
     * Description: this method collects user IMEI of the device, storing it into imei attribute, and
     * returning it as a string
     * @return String
     */
    public String getIMEI() {
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            return telManager.getDeviceId();
        }
        catch(SecurityException e){
            Log.e(TAG,MyAdView.errMsg[1]+"fetch IMEI");
            return null;
        }
    }

    /**
     * Description: this method collects advertising id of the device, storing it in the class and
     * return it as a string
     * @return String
     */
    public String getAdvertisingId() {
        return new AdIdGetter().getAd_id();
    }

    /**
     * Description: this method get the GPS location of the device, storing it to loc attribute
     * in the class, and return it. You can call getLatitude() and getLongitude() of the returned object
     * to get the GPS info. the return type of these is double
     * @return Location object
     */
    public Location getLocation(){
        return new LocationTracker(ctx).getLocation();
    }
}
