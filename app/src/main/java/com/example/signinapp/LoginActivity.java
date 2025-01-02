package com.example.signinapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.signinapp.databinding.ActivityLoginBinding;
import controller.DBHelper;
import controller.LoginController;
import module.Utils;

/**
 * Description: this class is responsible for user login. It provides a user login interface
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private static String TAG="CourseWork:Login";
    private DBHelper dbHelper=new DBHelper(this);

    /**
     * Description: this method aggregates all the methods needed for login
     * @param saveInstanceState
     */
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //jump to the registration page when the text is clicked
        Utils.backLogic(binding.notRegistered,LoginActivity.this,RegisterActivity.class);

        loginBtnLogic();
        Utils.backLogic(binding.back,LoginActivity.this,MainActivity.class);
    }

    /**
     * Description: the after the login button is clicked, messages from the boxes are retrieved and
     * analyzed. If the data pass the validation and verification tests, then login successful!
     */
    private void loginBtnLogic(){
        LoginController loginCtrl=new LoginController(this);
        binding.loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
//                List<HashMap<String,String>> list=dbHelper.getTable();
//                for(int i=0;i<list.size();i++){
//                    HashMap<String,String> hmap=list.get(i);
//                    for(Map.Entry<String,String> entry:hmap.entrySet()){
//                        System.out.println("key:"+entry.getKey()+"; value:"+entry.getValue());
//                    }
//                }
                String uname=binding.uname.getText().toString();
                String givenPwd=binding.pwd.getText().toString();

                //detect if input box(es) is(are) empty
                if(loginCtrl.emptyBox(uname,givenPwd)){
                    Log.d(TAG,"empty box");
                    return;
                }
                try {
                    /*
                    every HashMap<String,String> represents a record. There should only be one record
                    in the 'results' list as the username is unique.
                     */
                    //query the fields with respect to the entered username
                    List<HashMap<String,String>> results=dbHelper.queryData(new String[]{"uname",
                            "portrait_path", "pwd","warning_lv","email"}, new String[]{"uname",uname});

                    if(!loginCtrl.queryResultCheck(results)){
                        Log.d(TAG,"username not found");
                        return;
                    }
                    HashMap<String,String> resultMap=results.get(0);

                    //check if the password matches
                    if(!loginCtrl.pwdCheck(givenPwd,resultMap.get("pwd"))){
                        Log.d(TAG,"password incorrect");
                        return;
                    }
                    /*/////////////////////
                    verification successful
                    */////////////////////
                    //write all the data into
                    Log.d(TAG,resultMap.get("portrait_path"));
                    loginCtrl.write(resultMap);
                    loginCtrl.outputText("login successful!");
                    Log.d(TAG,"login successful");
                    Utils.jump2Activity(LoginActivity.this,MainActivity.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
