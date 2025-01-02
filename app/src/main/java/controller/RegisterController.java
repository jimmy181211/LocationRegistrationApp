package controller;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Description: this class contains all the logic for registration
 */
public class RegisterController {
    private static final String TAG="CourseWork:Register";
    private DBHelper dbHelper;
    private Context ctx;

    public RegisterController(Context ctx){
        this.ctx=ctx;
        this.dbHelper=new DBHelper(ctx);
    }

    /**
     * Description: this method verifies whether the two passwords the user entered is true or false
     * @return a boolean that indicates whether the two passwords are the same
     */
    public boolean verifyPwd(String pwd1, String pwd2){
        if(!pwd1.equals(pwd2)){
            Log.d(TAG,"password not matching");
            return false;
        }
        return true;
    }

    /**
     * Description: this method checks if the about-to-insert data of fields that are marked 'unique'
     * have existed values in the table. It outputs all fields that has repeated values and flags a
     * true if at least one field has repeated value and returns false if all fields are unique.
     * @param uname username entered
     * @param email email entered
     * @param phoneNo phone number entered
     * @return a boolean, whether there is at least one field repeated or no repeated fields.
     */

    public boolean checkIfExists(String uname, String email, String phoneNo){
        //create a ContentValues Object
        ContentValues vals=new ContentValues();
        vals.put("uname",uname);
        vals.put("email",email);
        vals.put("phone_no",phoneNo);

        List<HashMap<String,String>> table=dbHelper.getTable();
        Object[] keys=vals.keySet().toArray();
        //this list will be involved in the operations
        int origLen=keys.length;
        List<String> attrs=new ArrayList<>(origLen);
        HashMap<String, Boolean> isExist=new HashMap<>();//this array determines which key's value exists in the table
        for(Object key:keys){
            attrs.add((String)key);
            isExist.put((String)key, false);
        }
        for(int i=0;i<table.size();i++){
            HashMap<String,String> record=table.get(i);//retrieve records from the table
            //if all input fields are confirmed to be repeated, then there is no need to continue the loop
            if(attrs.isEmpty()){
                break;
            }
            for(int j=0;j<attrs.size();j++){
                String attr=attrs.get(j);
                //if the login info that should be unique exists in the table
                if(Objects.equals(record.get(attr), vals.get(attr))){
                    isExist.put(attr,true);
                    attrs.remove(attr);//remove the item that is confirmed existed
                    j--;
                }
            }
        }
        //if the 'attrs' list was modified, it means that at least one element in the list exists in the table
        if(attrs.size()!=origLen){
            //design the text shown to the user
            StringBuffer txt=new StringBuffer("the ");
            for(int i=0;i<isExist.size();i++){
                String name=(String)keys[i];
                if(Boolean.TRUE.equals(isExist.get(name))){
                    txt.append(name).append(", ");
                }
            }
            txt.append("exists already");
            outputText(txt.toString());
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Description: this method takes the data from the input boxes, encapsulates them into an object
     * and stores it into the table
     * @param uname username entered
     * @param pwd password entered
     * @param email email entered
     * @param age age entered (a number)
     * @param phone_no phone number entered (a UK phone number)
     */
    public void storeRegisterInfo(String uname, String pwd, String email, String age, String phone_no,String warning_lv){
        ContentValues vals=new ContentValues();
        //retrieve and store user inputs to the ContentValues object
        vals.put("uname",uname);
        vals.put("pwd",pwd);
        vals.put("email",email);
        vals.put("age", age);
        vals.put("warning_lv",warning_lv);
        vals.put("phone_no",phone_no);
        vals.put("portrait_path",FileHandler.DEFAULT_PORTRAIT_PATH);
        //store the values to the database
        dbHelper.store(vals);
        Log.d(TAG,"store information successful");
    }

    public void outputText(String text){
        Toast.makeText(ctx,text,Toast.LENGTH_SHORT).show();
    }

    /**
     * Description: this public method does regular registration checks
     * @param uname the username entered
     * @param pwd the password entered
     * @param pwd2 the second password (for verification purposes) entered
     * @return a boolean, whether the check is passed or not.
     */
    public boolean registerCheck(String uname, String pwd, String pwd2, String email, String phone_no){
        //length check
        if(uname.length()>DBHelper.maxUnameLen){
            outputText("the username shouldn't contain more than "+DBHelper.maxUnameLen.toString()+" characters");
        }

        //length check
        else if(pwd.length()<DBHelper.leastPwdLen){
            outputText("the password length should be bigger than 4!");
        }
        //verify whether the two passwords entered are the same
        else if(!verifyPwd(pwd,pwd2)){
            outputText("passwords not matching, please try again");
        }
        else if(!isValidEmail(email)){
            outputText("the email address is invalid");
        }
        else if(!isValidPhoneNo(phone_no)){
            outputText("the phone number is invalid");
        }
        else{
            return true;
        }
        return false;
    }

    /**
     * Description: this method checks if the email is valid
     * @param email email entered by the user
     * @return true if the email is valid, false if not
     */
    public boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    /**
     * Description: this method checks if the phone number is valid
     * @param phone_no phone number entered by the user
     * @return true if the phone number if valid, false if not
     */
    public boolean isValidPhoneNo(String phone_no){
        if(phone_no.length()==DBHelper.phone_noLen && phone_no.charAt(0) == '0'){
            return true;
        }
        else{
            return false;
        }
    }
}
