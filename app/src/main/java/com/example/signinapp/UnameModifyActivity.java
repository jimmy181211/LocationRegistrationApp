package com.example.signinapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.signinapp.databinding.ActivityUnameModifyBinding;

import controller.DBHelper;
import controller.FileHandler;
import controller.UserInfo;
import module.Utils;

public class UnameModifyActivity extends AppCompatActivity {
    private ActivityUnameModifyBinding binding;
    private static final String TAG="CourseWork:UnameModify";
    private String unameStr;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=ActivityUnameModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set text to the input box
        unameStr=getIntent().getStringExtra("username");
        binding.newUname.setText(unameStr);

        Utils.backLogic(binding.back,UnameModifyActivity.this, SettingActivity.class);
        //when the user clicks confirm then save the changes
        confirmLogic();
    }

    /**
     * Description: this method provides a button listener. When it is clicked, the new username will
     * be sent to the setting activity
     */
    private void confirmLogic(){
        binding.confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String newUname=binding.newUname.getText().toString();
                if(newUname.length()>50){
                    Toast.makeText(UnameModifyActivity.this,"the new name is too long, " +
                            "it should be no longer than 50 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                //change the username in the table record and update the table
                DBHelper dbHelper=new DBHelper(UnameModifyActivity.this);
                dbHelper.modify(new String[]{"uname",newUname},new String[]{"uname",unameStr});

                //write the changes into the local file as well
                UserInfo uInfo=FileHandler.readFile(UnameModifyActivity.this);
                uInfo.setUsername(newUname);
                FileHandler.writeFile(UnameModifyActivity.this,uInfo);
                jump2Setting(newUname);
            }
        });
    }

    private void jump2Setting(String newUname){
        Intent intent=new Intent(UnameModifyActivity.this,SettingActivity.class);
        intent.putExtra("newUname",newUname);
        startActivity(intent);
    }
}
