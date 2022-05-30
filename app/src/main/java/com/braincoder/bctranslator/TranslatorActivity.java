package com.braincoder.bctranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    TranslatorOptions translatorOptions;
    Translator translator;
    private String[] languages = new String[]{"English", "Urdu"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initButtonClicks();
        setLanguageAdapters();
    }

    private void init(){
        // Create translator
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.URDU)
                .build();
        translator = Translation.getClient(translatorOptions);

//        // Download model if needed
//        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Log.i("Success = ", "Model Downloaded");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                Log.i("Failed = ", "Model download failed");
//            }
//        });

    }

    private void initButtonClicks(){
        binding.translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(binding.sourceBox.getText().toString());
            }
        });

        binding.swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int source = binding.sourceLanguage.getSelectedItemPosition();
                int target = binding.targetLanguage.getSelectedItemPosition();

                binding.sourceLanguage.setSelection(target);
                binding.targetLanguage.setSelection(source);

                changeLanguage();
            }
        });

        binding.sourceLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.targetLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    private void setLanguageAdapters(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        binding.sourceLanguage.setAdapter(arrayAdapter);
        binding.targetLanguage.setAdapter(arrayAdapter);

        binding.targetLanguage.setSelection(1);
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

    private void changeLanguage(){
        String fromLanguage = languages[binding.sourceLanguage.getSelectedItemPosition()];
        String toLanguage = languages[binding.targetLanguage.getSelectedItemPosition()];

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(getLanguageCode(fromLanguage))
                .setTargetLanguage(getLanguageCode(toLanguage))
                .build();
        translator = Translation.getClient(translatorOptions);
    }

    private String getLanguageCode(String language){
        switch (language){
            case "English":
                return TranslateLanguage.ENGLISH;
            case "Urdu":
                return TranslateLanguage.URDU;
            default:
                return TranslateLanguage.ENGLISH;
        }
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}