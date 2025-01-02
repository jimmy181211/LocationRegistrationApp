package com.example.myadlib;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.ArrayList;

public class AudioRecord {
    public static int RESULT_SPEECH=1;

//    public static void initiateRecord(Context ctx){
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
//        ctx.startActivityForResult(intent,RESULT_SPEECH);
//    }
    public static void handleResult(int requestCode, int resultCode, Intent data){
        if(requestCode!=RESULT_SPEECH || resultCode!=RESULT_OK){
            return;
        }
        ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if(result!=null && !result.isEmpty()){
            for(int i=0;i<result.size();i++){
                Log.i("CourseWork:AudioRecord",result.get(i));
            }
        }
    }
}
