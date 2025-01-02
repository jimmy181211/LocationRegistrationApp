package controller;

import android.util.Log;

import com.example.signinapp.R;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Description: this class stores all the user information needed in the app. It encapsulates the
 * information and provides getter and setter methods to access or modify them
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 100L;
    private String username;
    private String portrait_path;
    private String warning_lv;
    private boolean is_login;
    private String email;
    private HashMap<String,Integer> colorMap=createColorMap();

    private static HashMap<String,Integer> createColorMap(){
        HashMap<String,Integer> colorMap=new HashMap<>();
        colorMap.put("0",R.color.warning_level0);
        colorMap.put("1", R.color.warning_level1);
        colorMap.put("2",R.color.warning_level2);
        colorMap.put("3",R.color.warning_level3);
        return colorMap;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String givenEmail){
        this.email=givenEmail;
    }

    public Integer getWarning_lvColor(){
        return colorMap.get(warning_lv);
    }

    public void setUsername(String newUsername){
        this.username=newUsername;
    }

    public String getUsername(){
        return username;
    }

    public String getPortrait_path() {
        return portrait_path;
    }

    public void setPortrait_path(String portrait_path) {
        this.portrait_path = portrait_path;
    }

    public String getWarning_lv() {
        return warning_lv;
    }

    public void setWarning_lv(String warning_lv) {
        this.warning_lv = warning_lv;
    }

    public Boolean getIs_login() {
        return is_login;
    }

    public void setIs_login(boolean is_login) {
        this.is_login = is_login;
    }
}
