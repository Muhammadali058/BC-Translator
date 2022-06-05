package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){

    }

}