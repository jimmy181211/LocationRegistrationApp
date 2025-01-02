package com.example.myadlib;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by soteris on 18/02/24. refined by Jimmy 28/7/2024
 */
public class MyAdView {
    public static boolean isLAT = false;
    public static Context ctx;
    protected static String[] errMsg = {"error message", "doesn't have permission to "};
    private static String TAG="CourseWork:MyAdView";
    private static String uname;
    @SuppressLint("StaticFieldLeak")
    private static InfoGetter infoGetter=null;
    private static InfoManager infoManager=new InfoManager();
    public static final int MAIN_STOP_CODE=1245;
    public static final int MAIN_START_CODE=1425;

    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==BGService.MSG_CODE && infoGetter!=null){
                infoManager.addLocation(infoGetter.getLocation());
            }
            else if(msg.what==MAIN_STOP_CODE){
                //stop the background service
                BGService.stop();
                ctx.stopService(new Intent(ctx,BGService.class));
            }
            else if(msg.what==MAIN_START_CODE){
                BGService.start();
                Intent intent=new Intent(ctx,BGService.class);
                //start a background service
                ctx.startService(intent);
            }
        }
    };

    public static void loadAd(ImageView iv, Context ctx, String uname) {
        MyAdView.ctx = ctx;
        MyAdView.uname=uname;
        Log.d(TAG,MyAdView.uname);
        infoGetter=new InfoGetter(ctx);

        // Ad library background logic
        onLoad();

    }

    /**
     * Description: this method get app names and last installed date using packageManager, and
     * stores the information into a list. Every element in the list is a record of type
     * ContentValues (key-value pair).
     * @return a list of ContentValues object
     */
    private static List<ContentValues> getAllApps(){
        PackageManager pm=ctx.getPackageManager();
        List<ApplicationInfo> infos=pm.getInstalledApplications(PackageManager.GET_META_DATA);
        //create a list with size of total number of apps
        List<ContentValues> apps=new ArrayList<>();
        ContentValues tempVals;
        //add all the third-party app name in string list
        for(ApplicationInfo info:infos) {
            if((info.flags & ApplicationInfo.FLAG_SYSTEM)==0){
                tempVals=new ContentValues();
                //get the app name corresponds to the package name
                tempVals.put("app_name", (String) pm.getApplicationLabel(info));
//                tempVals.put("type","none");
                long lastModifiedDate=new File(info.sourceDir).lastModified();
                String formattedDate=Pack.dateFormatter(new Date(lastModifiedDate));
                tempVals.put("installed_date",formattedDate);
                apps.add(tempVals);
            }
        }
        return apps;
    }

    /**
     * Description: some program logic will be loaded here
     */
    private static void onLoad() {
        //get all installed third-party apps
        List<ContentValues> apps=getAllApps();
        //store it to the database
        DBHelper dbHelper=new DBHelper(ctx,DBHelper.TableType.USERS_TABLE,null);

        DBHelper dbHelper2=new DBHelper(ctx,DBHelper.TableType.SINGLE_USER_TABLE,uname);

        // store app information into the sqLite database
        for(int i=0;i<apps.size();i++){
            if(apps.get(i)!=null){
                dbHelper2.store(apps.get(i), dbHelper2.APP_TABLE);
            }
        }
    }
}
