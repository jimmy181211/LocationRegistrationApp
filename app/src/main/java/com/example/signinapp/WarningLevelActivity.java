package com.example.signinapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.signinapp.databinding.ActivityWarningLevelBinding;

import java.util.HashMap;

import module.Utils;

/**
 * Description: this class provides the UI for Warning level detail description, for example, the
 * privileges you lose/ the colour corresponds to each warning level. The page is static and a 'back'
 * button is provided to quit the page
 */
public class WarningLevelActivity extends AppCompatActivity {
    private ActivityWarningLevelBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWarningLevelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utils.backLogic(binding.back,WarningLevelActivity.this,SettingActivity.class);
    }
}
