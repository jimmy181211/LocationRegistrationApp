package com.example.myadlib;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: this class encapsulates the data-retrieved time and data content as a whole
 */
public class Pack implements Serializable {
    private Object data;
    public String curTime;

    public Pack(){}
    public Pack(Object data){
        setData(data);
    }

    public Object getData(){
        return data;
    }

    /**
     * Description: this method formats the date
     * @param date date of type Date
     * @return formatted date (a string)
     */
    @SuppressLint("SimpleDateFormat")
    public static String dateFormatter(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        return sdf.format(date);
    }

    /**
     * when storing the data, the current time will be recorded and formatted. The programmer using
     * the class doesn't know how it works inside. Operations are concealed
     * @param data the data to be stored
     */
    public void setData(Object data){
        this.data=data;
        curTime=dateFormatter(new Date());
    }
}
