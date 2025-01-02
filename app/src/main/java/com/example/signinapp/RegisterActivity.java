package com.example.signinapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.signinapp.databinding.ActivityRegisterBinding;

import controller.RegisterController;
import module.Utils;

/**
 * Description: This class is responsible for the registration process. It gets user data and store
 * it to the specific database.
 * @version 2.0
 */
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private static final String TAG="CourseWork:Register";

    /**
     * Description: this method is a collection of other method (logic pieces) for registration
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //register logic
        registerLogic();
        //back logic
        Utils.backLogic(binding.back,RegisterActivity.this,MainActivity.class);
    }

    /**
     * Description: this method uses methods from RegisterController. It fetches the data that the
     * user entered, and analyze it. If all data are valid, then the person will be registered and
     * the data is stored into the table using the DBHelper
     */
    private void registerLogic(){
        //instantiate a controller object and call register logic from it
        RegisterController rCtrl=new RegisterController(this);

        binding.registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String uname=binding.uname.getText().toString();
                String pwd=binding.pwd1.getText().toString();
                String email=binding.email.getText().toString();
                String phoneNo=binding.phoneNo.getText().toString();

                //validation and verification for the entered information
                if(!rCtrl.registerCheck(uname,pwd,binding.pwd2.getText().toString(),email,phoneNo)){
                    return;
                }

                //ensure that certain fields are unique in the user table (if the data exists)
                if(rCtrl.checkIfExists(uname,email,phoneNo)){
                    Log.d(TAG,"fields that are marked unique exists in the table already!");
                    return;
                }

                //store the data into the database
                rCtrl.storeRegisterInfo(
                        uname,
                        pwd,
                        email,
                        binding.age.getText().toString(),
                        phoneNo,
                        "0"
                );

                rCtrl.outputText("register successful!");
                //jump to user login page after the successful registration
                Utils.jump2Activity(RegisterActivity.this,LoginActivity.class);
            }
        });
    }
}
