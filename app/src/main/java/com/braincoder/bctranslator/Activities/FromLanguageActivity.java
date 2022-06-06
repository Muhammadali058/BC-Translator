package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.braincoder.bctranslator.R;

public class FromLanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_language);
        this.setFinishOnTouchOutside(true);
    }
}