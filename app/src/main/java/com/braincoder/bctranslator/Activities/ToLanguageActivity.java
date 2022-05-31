package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.braincoder.bctranslator.R;

public class ToLanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_language);
        
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent){
        boolean readOnly = true;
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT_READONLY)) {
             readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);

            Toast.makeText(this, "Readonly " + readOnly, Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            String text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();

            Toast.makeText(this, "Called with " + text, Toast.LENGTH_SHORT).show();
            replaceText(text.toUpperCase());
        }
    }

    private void replaceText(String text){
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}