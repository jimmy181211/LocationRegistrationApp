package com.example.signinapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.example.myadlib.AudioRecord;
import com.example.myadlib.MyAdView;
import com.example.signinapp.databinding.ActivityMainBinding;

import controller.DBHelper;
import controller.FileHandler;
import controller.UserInfo;
import module.Utils;

/**
 * Created by soteris on 18/02/24.
 * refined by Jimmy (Jinhong Ouyang) on 24/7/24
 */
public class MainActivity extends AppCompatActivity{
    //************************************************************************//
    //************************************************************************//
    //***********************       GLOBALS          *************************//
    //************************************************************************//
    // PermissionsHandler
    AlertDialog alertDialog;
    ArrayList<String> permissionsList = new ArrayList<>();
    String[] permissionsStr = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    int permissionsCount = 0;
    private ActivityMainBinding binding;
    //the name of the file that caches frequently used user information
    private UserInfo uInfo;

    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onActivityResult(Map<String,Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                }else if (!hasPermission(MainActivity.this, permissionsStr[i])){
                                    permissionsCount++;
                                }
                            }
                            if (permissionsList.size() > 0) {
                                //Some permissions are denied and should be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                            } else {
                                //All permissions granted.
                                permissionsGrantedLogic(uInfo.getUsername());
                            }
                        }
                    });
    //////////////

    //************************************************************************//
    //************************************************************************//
    //***********************       MAIN          *************************//
    //************************************************************************//
    private final static String TAG="Jimmy CW:MainActivity";

    private String createNotificationChannel(String channelId,String channelName,int level){
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(channelId,channelName,level);
            channel.setDescription("message level: "+level);
            manager.createNotificationChannel(channel);
        }
        return channelId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Description: the click event logic for alarming (for location sending). If the
     * user is not logged in, then he doesn't have privileges to use this function
     */
    private void alarmBtnLogic(){
        binding.alarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //if the user doesn't login, then this function is not available
                if(uInfo.getIs_login()){
                    Utils.jump2Activity(MainActivity.this,AlarmActivity.class);
                }
                else{
                    Toast.makeText(MainActivity.this, "you need to login first!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Description: the click event logic for setting. If clicked, then jump to the SettingActivity
     */
    private void settingBtnLogic(){
        binding.setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Utils.jump2Activity(MainActivity.this,SettingActivity.class);
            }
        });
    }

    /**
     * Description: at onStart stage, requestPermissions() is re-triggered. This is when logging
     */
    @Override
    public void onStart() {
        super.onStart();
        requestPermissions();
        uInfo= FileHandler.readFile(this);
        if(uInfo==null){
            Log.d(TAG,"uInfo is null");
        }
        Log.d(TAG,uInfo.getPortrait_path());
        createNotificationChannel("@me","@myNotification",NotificationManager.IMPORTANCE_HIGH);

        /*set the layout for the RHS widget bar*/
        //set widget layouts
        float widgetSize = getResources().getDimension(R.dimen.widget_size);
        float widgetMargin=getResources().getDimension(R.dimen.widget_margin);

        ViewGroup.LayoutParams layoutParams=binding.widgetLayout.getLayoutParams();
        layoutParams.height= Utils.dp2px(this,160);
        binding.widgetLayout.setLayoutParams (layoutParams);

        //set the position of the alarm
        ViewGroup.MarginLayoutParams alarmLayout=(ViewGroup.MarginLayoutParams)binding.alarm.getLayoutParams();
        alarmLayout.setMargins(0, (int) (widgetSize+widgetMargin),0,0);

        /*set the onClick event for the setting button. It navigates the user to the setting activity
         */
        settingBtnLogic();

        alarmBtnLogic();
//
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
//        startActivityForResult(intent,AudioRecord.RESULT_SPEECH);
    }

    /**
     * Description: after the permissions are granted, ads are loaded
     * @param uname the username will be passed to the advert library. So it can bind the user data fetched with the username
     */
    private void permissionsGrantedLogic(String uname) {
        // Monetization //
        MyAdView.loadAd(binding.ivAd, MainActivity.this,uname);
        MyAdView.handler.sendEmptyMessage(MyAdView.MAIN_START_CODE);

        // App Logic //
        // ...
    }

    //************************************************************************//
    //************************************************************************//
    //*****************          PERMISSION HANDLING          ****************//
    //************************************************************************//
    // PermissionsHandler
    private void requestPermissions() {
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("All requested permissions must be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                });

        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        AudioRecord.handleResult(requestCode,resultCode,data);
    }

    public static void saveChanges(Context ctx){
        UserInfo uInfo=FileHandler.readFile(ctx);
        //update the database
        DBHelper dbHelper=new DBHelper(ctx);
        String[] idPair=new String[]{"email",uInfo.getEmail()};
        dbHelper.modify(new String[]{"uname",uInfo.getUsername()}, idPair);
        dbHelper.modify(new String[]{"portrait_path",uInfo.getPortrait_path()}, idPair);
        dbHelper.modify(new String[]{"warning_lv",uInfo.getWarning_lv()}, idPair);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //stop the location fetcher in the ad library
        MyAdView.handler.sendEmptyMessage(MyAdView.MAIN_STOP_CODE);
        saveChanges(this);
    }
    ///////////////
    //************************************************************************//
    //************************************************************************//
}