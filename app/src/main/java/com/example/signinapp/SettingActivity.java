package com.example.signinapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.signinapp.databinding.ActivitySettingBinding;

import java.util.HashMap;
import java.util.List;

import controller.DBHelper;
import controller.FileHandler;
import controller.PortraitGetter;
import controller.UserInfo;
import module.Utils;

/**
 * Description: this class provides a UI for setting
 */
public class SettingActivity extends AppCompatActivity {
    private static final String TAG="CourseWork:Setting";
    private ActivitySettingBinding binding;
    private PortraitGetter portraitGetter;
    private static final String noPrivilegeNotice="you need to login first!";
    private Boolean isLoginUser=false;
    private UserInfo uInfo;
    private static final int logOutMsg=1532;//the id used to identify a log-out message

    @SuppressLint("HandlerLeak")
    public Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //sign out logic execution
            if(msg.what==logOutMsg){
                //save the changes made on the personal information
                MainActivity.saveChanges(SettingActivity.this);
                //recover the setting
                FileHandler.unLoginFileSetting(SettingActivity.this);
                Toast.makeText(SettingActivity.this,"sign out successful!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingActivity.this,MainActivity.class));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"setting activity is started");
        super.onCreate(savedInstanceState);
        //set bindings
        binding=ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //read userinfo from the local file
        uInfo = FileHandler.readFile(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        assert uInfo != null;
        String portraitFile=uInfo.getPortrait_path();
        String unameStr=uInfo.getUsername();
        Log.d(TAG,"the username is:"+unameStr);
        this.isLoginUser=uInfo.getIs_login();

        //get the new username from UnameModifyActivity
        Intent intent=getIntent();
        String newUname=intent.getStringExtra("newUname");
        //if the user updates the username via UnameModifyActivity reset username
        if(newUname!=null){
            unameStr=newUname;
        }

        portraitGetter=new PortraitGetter(this,binding.portrait);
        //set the portrait to whatever is preserved via setImage() from PortraitGetter
        portraitGetter.setPortrait(portraitFile);
        //setting user portrait logic
        setUserPortraitLogic();

        //going back to MainActivity logic
        Utils.backLogic(binding.back,SettingActivity.this,MainActivity.class);

        //warning level explanation page logic
        Utils.backLogic(binding.warningLevelColor,SettingActivity.this,WarningLevelActivity.class);

        //reset username logic
        setUnameLogic(unameStr);

        binding.confirmButton.setText(isLoginUser?"sign out":"sign in");
        //confirm button clicked logic:
        confirmLogic();
    }

    /**
     * Description: this method provides a UI for the username setting entrance. When clicking the
     * username,the program will jump to UnameModifyActivity for a username reset
     * @param unameStr the current username string
     */
    private void setUnameLogic(String unameStr){
        //set the warning level. The relevant data are transmitted from the server

        binding.warningLevelColor.setBackgroundColor(getResources().getColor(uInfo.getWarning_lvColor()));
        binding.warningLevelTxt.setText("warning level");

        //set underline to the username text
        binding.uname.setText(unameStr);
        //underline the name
        binding.uname.setPaintFlags(binding.uname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //reset username logic
        binding.uname.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //only the logged in user have the privilege to modify username
                if(isLoginUser){
                    jump2UnameModif(unameStr);
                }
                else{
                    Toast.makeText(SettingActivity.this,noPrivilegeNotice, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Description: the code logic that enables jumping from the current activity to UnameModifyActivity,
     * attached with the current username
     * @param unameStr the current username string
     */
    private void jump2UnameModif(String unameStr){
        Intent intent=new Intent(SettingActivity.this,UnameModifyActivity.class);
        intent.putExtra("username",unameStr);
        startActivity(intent);
    }

    /**
     * Description: this method provides a button and listener for login/logout request.
     */
    private void confirmLogic(){
        binding.confirmButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //log out
                if(isLoginUser){
                    Utils utils=new Utils(handler);
                    ////////////use a dialog box to confirm this change//////////////
                    utils.dialog("confirm the change","do you want to sign out?",
                            SettingActivity.this,logOutMsg);
                }
                //log in
                else{
                    //jump to the login page
                    startActivity(new Intent(SettingActivity.this,LoginActivity.class));
                }
            }
        });
    }

    /**
     * Description: this method provides a UI for user portrait setting. When the portrait is clicked,
     * the program will jump to the album for a portrait reset
     */
    private void setUserPortraitLogic(){
        binding.portrait.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String word;
                //only the logged in user have the privilege to reset portrait
                if(isLoginUser){
                    word="select image";
                    portraitGetter.selectImage();
                    if(portraitGetter.imgPath!=null){
                        Log.d(TAG,portraitGetter.imgPath);
                    }
                    //set the new portrait_path
                    UserInfo uInfo=FileHandler.readFile(SettingActivity.this);
                    uInfo.setPortrait_path(portraitGetter.imgPath);
                    //modify the uri string stored in the table
                }
                else{
                    word=noPrivilegeNotice;
                }
                Toast.makeText(SettingActivity.this,word,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Description: logic related to portrait settings...
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        portraitGetter.onRequestPermissionResultComponent(requestCode,grantResults);
    }

    /**
     * Description: receive the result from the system album
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        portraitGetter.onActivityResultComponent(requestCode,resultCode,data);
    }
}
