package com.example.myadlib;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

import java.io.Serializable;

public class InfoHolder{
    private final DBHelper dbHelper;
    private static String TAG="CourseWork: InfoHolder";

    public InfoHolder(Context ctx,String tableName){
        this.dbHelper =new DBHelper(ctx, DBHelper.TableType.USERS_TABLE,null);
    }

    /**
     * Description: this method stores the location into the database
     * @param location from which latitude and longitude can be fetched
     */
    public void storeLocation(Location location){
        ContentValues vals=new ContentValues();
        vals.put("latitude",location.getLatitude());
        vals.put("longitude",location.getLongitude());
        dbHelper.store(vals,dbHelper.LOCATION_TABLE);
    }

    /**
     * this method stores the IMEI of the device into the database
     * @param imei the number string is used by the service provider to uniquely identify a mobile device.
     */
    public void storeStaticInfo(String uname, String imei,String ad_id){
        ContentValues vals=new ContentValues();
        vals.put("username",uname);
        vals.put("IMEI",imei);
        vals.put("ad_id",ad_id);
        dbHelper.store(vals,dbHelper.USERS_TABLE);
    }
}
