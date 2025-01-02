package com.example.myadlib;

import android.os.AsyncTask;
import android.util.Log;


import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Description: this class gets the advert id
 */
class AdIdGetter extends AsyncTask<String, Integer, String> {
    private final static String TAG="CourseWork: AdIdGetter";
    private String ad_id= null;

    @Override
    protected String doInBackground(String... strings) {
        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(MyAdView.ctx);
        } catch (IOException | GooglePlayServicesRepairableException |
                 GooglePlayServicesNotAvailableException e)
        {
            Log.e(MyAdView.errMsg[0],MyAdView.errMsg[1]+" can't fetch advertising id");
        }
        if(adInfo!=null) {
            MyAdView.isLAT = adInfo.isLimitAdTrackingEnabled();
            ad_id=adInfo.getId();
            return ad_id;
        }
        else{
            Log.e("error message","adInfo is null");
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "AdvertisingID: " + result);
    }

    public String getAd_id(){
        return this.ad_id;
    }
}
