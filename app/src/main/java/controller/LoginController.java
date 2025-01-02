package controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.signinapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Description: this class contains all the login logic
 */
public class LoginController {
    private Context ctx;
    private static final String TAG="CourseWork:LoginA";

    public LoginController(Context ctx){
        this.ctx=ctx;
    }

    /**
     * Description: this method determines whether the uname or password boxes are empty
     * @param uname username entered
     * @param pwd password entered
     * @return true if at least one of the boxes is empty, false if all boxes are not empty
     */
    public boolean emptyBox(String uname, String pwd){
        if(uname.isEmpty() && pwd.isEmpty()){
            outputText("username and password are empty");
        }
        else if(uname.isEmpty()){
            outputText("the username box is empty");
        }
        else if(pwd.isEmpty()){
            outputText("the password is empty");
        }
        else{
            return false;
        }
        return true;
    }

    /**
     * Description: check if the record information returned is valid
     * @param results the queried record(s)
     * @return whether the result is valid
     * @throws Exception if there are multiple records retrieved (using the same username), then throw an Exception because there is something wrong with the program
     */
    public boolean queryResultCheck(List<HashMap<String,String>> results) throws Exception{
        //a problem occurs within the database: there are repeated user name
        if(results.size()>1){
            throw new Exception("multiple identical usernames stored in the database, which is illegal");
        }

        //if the username doesn't exist
        else if(results.isEmpty()){
            Log.i(TAG,"the username is not found!");
            outputText("the username is not found! register first?");
        }
        else{
            return true;
        }
        return false;
    }

    /**
     * Description: this method is an encapsulation of toast. It shows a message on the UI
     * @param words the text that will be shown on the UI
     */
    public void outputText(String words){
        Toast.makeText(ctx,words,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Description: it check if the entered password equals to the actual password
     * @param givenPwd the password that the user entered
     * @param truePwd the password retrieved from the table that matches to the username
     * @return whether the two passwords are equal (if the password the user entered is correct)
     */
    public boolean pwdCheck(String givenPwd, @Nullable String truePwd){
        assert truePwd != null;

        //if the user-entered password doesn't match with the actual one:
        if(!truePwd.equals(givenPwd)){
            Log.i(TAG,"password incorrect!");
            outputText("password incorrect!");
            return false;
        }
        return true;
    }

    /**
     * Description: dump the userinfo into the local file, facilitating data retrieval
     * @param resultMap the key-value pairs fetched from the login page.
     */
    public void write(HashMap<String,String> resultMap){
        //read, modify then write the user file
        UserInfo uInfo= FileHandler.readFile(ctx);
        //reset all the data
        uInfo.setUsername(resultMap.get("uname"));
        uInfo.setIs_login(true);
        uInfo.setPortrait_path(resultMap.get("portrait_path"));
        uInfo.setWarning_lv(resultMap.get("warning_lv"));
        uInfo.setEmail(resultMap.get("email"));

        //write the userinfo into the local file
        FileHandler.writeFile(ctx, uInfo);
    }
}
