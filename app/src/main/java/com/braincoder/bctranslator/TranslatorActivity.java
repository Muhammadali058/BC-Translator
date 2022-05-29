package com.braincoder.bctranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.braincoder.bctranslator.databinding.ActivityTranslatorBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslatorActivity extends AppCompatActivity {

    ActivityTranslatorBinding binding;
    Translator translator;
    Translator translatorInverse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        // Create an English-Urdu translator:
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.URDU)
                .build();
        TranslatorOptions optionsInverse = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.URDU)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        translator = Translation.getClient(options);
        translatorInverse = Translation.getClient(optionsInverse);

        // Download model if needed
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i("Success = ", "Model Downloaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i("Failed = ", "Model download failed");
            }
        });

        // Initializing Buttons
        binding.translateDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(binding.sourceBox.getText().toString());
            }
        });

        binding.translateUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateInverse(binding.targetBox.getText().toString());
            }
        });

        binding.sourceCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.sourceBox.getText());
                if(clipData != null)
                    clipboardManager.setPrimaryClip(clipData);
            }
        });

        binding.sourceCutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.sourceBox.getText());
                if(clipData != null){
                    clipboardManager.setPrimaryClip(clipData);
                    binding.sourceBox.setText("");
                }
            }
        });

        binding.sourcePasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                binding.sourceBox.setText(text);
            }
        });

        binding.targetCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.targetBox.getText());
                if(clipData != null)
                    clipboardManager.setPrimaryClip(clipData);
            }
        });

        binding.targetCutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.targetBox.getText());
                if(clipData != null){
                    clipboardManager.setPrimaryClip(clipData);
                    binding.targetBox.setText("");
                }
            }
        });

        binding.targetPasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                binding.targetBox.setText(text);
            }
        });

    }

    private void translate(String text){
        translator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                binding.targetBox.setText(o.toString());

                                hideKeyboard();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.i("Error = ", e.getMessage());
                                Toast.makeText(TranslatorActivity.this, "Error in converting", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void translateInverse(String text){
        translatorInverse.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                binding.sourceBox.setText(o.toString());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.i("Error = ", e.getMessage());
                                Toast.makeText(TranslatorActivity.this, "Error in converting", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}