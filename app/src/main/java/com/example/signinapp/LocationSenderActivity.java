package com.example.signinapp;

import android.os.Bundle;
import android.util.Log;

/**
 * Description: this class sends the device location to the back-end
 */
import androidx.appcompat.app.AppCompatActivity;
public class LocationSenderActivity extends AppCompatActivity {
    private static String TAG="CourseWork:locationSender";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"location sent");
        //logic to send the location
    }
}
