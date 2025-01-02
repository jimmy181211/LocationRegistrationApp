package com.example.myadlib;

import android.location.Location;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Description: this class holds all the information collected from the device, and provides basic
 * setter and getter methods for the data
 * @version:2.0
 */

public class InfoManager{
    private final Pack imei=new Pack(), ad_id=new Pack();
    private final LinkedList<Pack> location=new LinkedList<>(),
            audioTranscripts=new LinkedList<>();

    public Pack getIMEI(){
        return imei;
    }

    public Pack getAd_id(){
        return ad_id;
    }

    public void setIMEI(String imei){
        this.imei.setData(imei);
    }

    public void setAd_id(String ad_id){
        this.ad_id.setData(ad_id);
    }

    /**
     * Description: this method retrieves the latest record of the device location from the
     * linked list and return it
     * @return Pack; pack is a data structure that stores varies meta data of the main data (here it's location)
     */
    public Pack getLocation(){
        return location.getLast();
    }

    public Pack getAudioTranscript(){
        return audioTranscripts.getLast();
    }

    public void addLocation(Location loc){
        location.add(new Pack(loc));
    }

    public void addAudioTranscript(String transcript){
        audioTranscripts.add(new Pack(transcript));
    }
}
